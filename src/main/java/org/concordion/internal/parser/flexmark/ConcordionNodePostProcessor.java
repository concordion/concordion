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

    private final String TARGET_CONCORDION_NAMESPACE_PREFIX;

    private final ReferenceRepository referenceRepository;
    private final ConciseExpressionParser statementParser;

    public ConcordionNodePostProcessor(DataHolder options) {
        this.referenceRepository = options.get(Parser.REFERENCES);
        TARGET_CONCORDION_NAMESPACE_PREFIX = CONCORDION_TARGET_NAMESPACE.getFrom(options);
        statementParser = new ConciseExpressionParser(SOURCE_CONCORDION_NAMESPACE_PREFIX,
                TARGET_CONCORDION_NAMESPACE_PREFIX,
                CONCORDION_ADDITIONAL_NAMESPACES.getFrom(options));
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Link) {
            visit(state, (Link) node);
        } else if (node instanceof LinkRef) {
            visit(state, (LinkRef) node);
        } else if (isExampleNode(node)) {
            visit(state, (Heading) node);
        }
    }

    private void visit(NodeTracker state, LinkRef node) {
        LinkRef linkRefNode = node;
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
            swapNode(state, linkRefNode, commandNode);
        }
    }

    private void visit(NodeTracker state, Link linkNode) {
        if (linkNode.getUrl().equals(URL_FOR_CONCORDION)) {
            BasedSequence title = linkNode.getTitle();
            BasedSequence text = linkNode.getText();
            if (isExpressionOnlyCommand(linkNode)) {
                text = BasedSequence.NULL;
            }
            ConcordionCommandNode commandNode = createNode(title, text);
            swapNode(state, linkNode, commandNode);
        } else {
            String runCommandToken = SOURCE_CONCORDION_NAMESPACE_PREFIX + ":run";
            if (linkNode.getTitle().startsWith(runCommandToken)) {
                String suffix = linkNode.getTitle().toString().substring(runCommandToken.length()).trim();
                String runner;
                if (suffix.startsWith("=")) {
                    runner = suffix.substring(1).trim();
                    if (runner.startsWith("'") || runner.startsWith("\"")) {
                        runner = runner.substring(1, runner.length() - 1);
                    }
                } else {
                    runner = "concordion";
                }

                ConcordionLinkNode newLinkNode = new ConcordionLinkNode();
                newLinkNode.setRunner(runner);
                newLinkNode.setCommand(TARGET_CONCORDION_NAMESPACE_PREFIX + ":run");
                newLinkNode.setUrl(linkNode.getUrl());
                newLinkNode.setChars(linkNode.getChars());
                Node firstChild = linkNode.getFirstChild();
                if (firstChild != null) {
                    newLinkNode.appendChild(firstChild);
                }
                swapNode(state, linkNode, newLinkNode);
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

    private void visit(NodeTracker state, Heading headerNode) {
        Link exampleNode = (Link) headerNode.getFirstChild();
        if (exampleNode.getUrl().equals(URL_FOR_CONCORDION)) {
            BasedSequence title = exampleNode.getTitle();
            BasedSequence text = exampleNode.getText();

            ConcordionExampleNode commandNode = createExampleNode(title, text);

            wrapNode(state, headerNode, commandNode);
            replaceNode(state, exampleNode, new Text(text));
            moveFollowingNodes(state, commandNode, headerNode);
        }
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

    private void swapNode(NodeTracker state, Node node, Node newNode) {
        node.insertAfter(newNode);
        node.unlink();
        state.nodeRemoved(node);
        state.nodeAddedWithChildren(newNode);
    }

    private void wrapNode(NodeTracker state, Node node, Node newNode) {
        node.insertBefore(newNode);
        node.unlink();
        state.nodeRemoved(node);
        newNode.appendChild(node);
        state.nodeAddedWithDescendants(newNode);
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
