package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.ReferenceRepository;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.collection.DataValueFactory;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.concordion.internal.parser.support.ConcordionStatement;

import java.util.Collections;
import java.util.Map;

public class ConcordionNodePostProcessor extends NodePostProcessor {
    public static final String URL_FOR_CONCORDION = "-";
    private static final String SOURCE_CONCORDION_NAMESPACE_PREFIX = "c";

    public static final DataKey<Map<String, String>> CONCORDION_ADDITIONAL_NAMESPACES = new DataKey<Map<String, String>>("CONCORDION_ADDITIONAL_NAMESPACES",
            new DataValueFactory<Map<String, String>>() {
                @Override
                public Map<String, String> create(DataHolder value) {
                    return Collections.EMPTY_MAP;
                }
            });
    public static final DataKey<String> CONCORDION_TARGET_NAMESPACE = new DataKey<String>("CONCORDION_TARGET_NAMESPACE", "concordion");

    private final ReferenceRepository referenceRepository;
    private final ConciseExpressionParser statementParser;

    public ConcordionNodePostProcessor(DataHolder options) {
        this.referenceRepository = options.get(Parser.REFERENCES);
        statementParser = new ConciseExpressionParser(SOURCE_CONCORDION_NAMESPACE_PREFIX,
                CONCORDION_TARGET_NAMESPACE.getFrom(options),
                CONCORDION_ADDITIONAL_NAMESPACES.getFrom(options));
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Link) {
            Link linkNode = (Link) node;
            if (linkNode.getUrl().equals(URL_FOR_CONCORDION)) {
                BasedSequence title = linkNode.getTitle();
                BasedSequence text = linkNode.getText();
                if (isExpressionOnlyCommand(linkNode)) {
                    text = BasedSequence.NULL;
                }
                ConcordionCommandNode commandNode = createNode(title, text);
                swapNode(state, node, commandNode);
            }
        } else if (node instanceof LinkRef) {
            LinkRef linkRefNode = (LinkRef) node;
            Reference referenceNode = linkRefNode.getReferenceNode(referenceRepository);
            if (referenceNode.getUrl().equals(URL_FOR_CONCORDION)) {
                BasedSequence title = referenceNode.getTitle();
                BasedSequence text = linkRefNode.getText();
                if (isExpressionOnlyCommand(linkRefNode)) {
                    text = BasedSequence.NULL;
                }
                if (text.isEmpty()) {
                    text = referenceNode.getReference();
                }
                ConcordionCommandNode commandNode = createNode(title, text);
                swapNode(state, node, commandNode);
            }
        } else if (isExampleNode(node)) {
            Heading headerNode = (Heading) node;
            Link exampleNode = (Link) node.getFirstChild();
            if (exampleNode.getUrl().equals(URL_FOR_CONCORDION)) {
                ConcordionExampleNode commandNode = createExampleNode(exampleNode.getTitle(), exampleNode.getText());
                wrapNode(state, node, commandNode);
                replaceNode(state, exampleNode, new Text(exampleNode.getText()));
                moveFollowingNodes(state, commandNode, headerNode);

//                    ConcordionStatement command = statementParser.parseCommandValueAndAttributes("example",
//                            exampleNode.getNodeName(), true);
            }
        }
    }

    private boolean isExpressionOnlyCommand(Node node) {
        return node.getFirstChild() instanceof Emphasis;
    }

    private ConcordionCommandNode createNode(BasedSequence title, BasedSequence text) {
        ConcordionStatement statement = statementParser.parse(title.toString(), text.toString());

        return ConcordionCommandNode.createNode(statement, title, text);
    }

    private ConcordionExampleNode createExampleNode(BasedSequence title, BasedSequence text) {
        String exampleName = title.toString();
        if (exampleName.isEmpty()) {
            exampleName = generateName(text.toString());
        }
        ConcordionStatement statement = statementParser.parseCommandValueAndAttributes("example", exampleName, true);
        return ConcordionExampleNode.createNode(statement);
    }

    private boolean isExampleNode(Node node) {
        return (node instanceof Heading && node.getFirstChild() instanceof Link);
    }

    private void swapNode(NodeTracker state, Node node, ConcordionCommandNode commandNode) {
        node.insertAfter(commandNode);
        node.unlink();
        state.nodeRemoved(node);
        state.nodeAddedWithChildren(commandNode);
    }

    private void wrapNode(NodeTracker state, Node node, ConcordionCommandNode commandNode) {
        node.insertBefore(commandNode);
        node.unlink();
        state.nodeRemoved(node);
        commandNode.appendChild(node);
        state.nodeAddedWithDescendants(commandNode);
    }

    private void replaceNode(NodeTracker state, Node toBeRemoved, Node toBeReplacedBy) {
        toBeRemoved.insertBefore(toBeReplacedBy);
        toBeRemoved.unlink();
        state.nodeAdded(toBeReplacedBy);
        state.nodeRemoved(toBeRemoved);
    }

    private void moveFollowingNodes(NodeTracker state, Node node, Heading headerNode) {
        Node followingNode = node.getNext();
        while (followingNode != null) {
            node.appendChild(node.getNext());
//            state.nodeRemoved(node.getNext());
            followingNode = node.getNext();
            if (isExampleNode(followingNode) ||
                    isHigherLevelHeadingThan(followingNode, headerNode)) {
                break;
            }
            if (isHeaderNodeWithStrikethrough(followingNode, headerNode)) {
                followingNode.unlink();
                state.nodeRemoved(followingNode);
                break;
            }
        }
        state.nodeAddedWithDescendants(node);
    }

    private boolean isHeaderNodeWithStrikethrough(Node followingNode, Heading headerNode) {
        if (followingNode instanceof Heading) {
            Node child = followingNode.getFirstChild();
            if (child instanceof Strikethrough) {
                Strikethrough strikethrough = (Strikethrough) child;
                return (strikethrough.getChildChars() != null && strikethrough.getChildChars().equals(headerNode.getChildChars()));
            }
        }
        return false;
    }

    // TODO check heading at same level - true or false?
    private boolean isHigherLevelHeadingThan(Node followingNode, Heading headerNode) {
        if (followingNode instanceof Heading) {
            Heading followingHeader = (Heading) followingNode;
            return (followingHeader.getLevel() < headerNode.getLevel());
        }
        return false;
    }

    private String generateName(String headingText) {
        StringBuilder sb = new StringBuilder(headingText.length());
        for (char c : headingText.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '-') {
                sb.append('-');
            }
        }
        String name = sb.toString();
        if (name.endsWith("-")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory(DataHolder options) {
            super(false);

            addNodes(Link.class);
            addNodes(LinkRef.class);
            addNodes(Heading.class);
        }

        @Override
        public NodePostProcessor create(Document document) {
            return new ConcordionNodePostProcessor(document);
        }
    }
}
