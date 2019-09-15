package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.concordion.internal.parser.support.ConcordionStatement;

public class ConcordionExampleNodePostProcessor extends NodePostProcessor {
    private final String TARGET_CONCORDION_NAMESPACE_PREFIX;
    private final ConciseExpressionParser statementParser;

    public ConcordionExampleNodePostProcessor(DataHolder options) {
        TARGET_CONCORDION_NAMESPACE_PREFIX = ConcordionMarkdownOptions.CONCORDION_TARGET_NAMESPACE.getFrom(options);
        statementParser = new ConciseExpressionParser(ConcordionMarkdownOptions.SOURCE_CONCORDION_NAMESPACE_PREFIX,
                TARGET_CONCORDION_NAMESPACE_PREFIX,
                ConcordionMarkdownOptions.CONCORDION_ADDITIONAL_NAMESPACES.getFrom(options));
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (isExampleNode(node)) {
            visit(state, (Heading) node);
        }
    }

    private boolean isExampleNode(Node node) {
        return (node instanceof Heading && node.getFirstChild() instanceof Link);
    }

    private void visit(NodeTracker state, Heading headerNode) {
        Link exampleNode = (Link) headerNode.getFirstChild();
        if (exampleNode.getUrl().equals(ConcordionMarkdownOptions.URL_FOR_CONCORDION)) {
            BasedSequence title = exampleNode.getTitle();
            BasedSequence text = exampleNode.getText();

            ConcordionExampleNode commandNode = createExampleNode(title, text);

            wrapNodeWith(state, headerNode, commandNode);
            replaceNode(state, exampleNode, new Text(text));
            moveFollowingNodesUntilExampleTerminated(state, commandNode, headerNode);
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

    private void wrapNodeWith(NodeTracker state, Node node, Node newNode) {
        node.insertBefore(newNode);
        node.unlink();
        state.nodeRemoved(node);
        newNode.appendChild(node);
        state.nodeAddedWithDescendants(newNode);
    }

    private void replaceNode(NodeTracker state, Node toBeRemoved, Node toBeReplacedBy) {
        toBeRemoved.insertBefore(toBeReplacedBy);
        toBeRemoved.unlink();
        state.nodeAddedWithDescendants(toBeReplacedBy);
        state.nodeRemoved(toBeRemoved);
    }

    private void moveFollowingNodesUntilExampleTerminated(NodeTracker state, Node node, Heading headerNode) {
        Node followingNode = node.getNext();
        while (followingNode != null) {
            node.appendChild(node.getNext());
            followingNode = node.getNext();
            if (isExampleNode(followingNode) ||
                    isHigherLevelHeadingThan(followingNode, headerNode)) {
                break;
            }
            if (isHeaderNodeWithStrikethroughTextMatching(followingNode, headerNode.getChildChars())) {
                followingNode.unlink();
                state.nodeRemoved(followingNode);
                break;
            }
        }
        state.nodeAddedWithDescendants(node);
    }

    private boolean isHeaderNodeWithStrikethroughTextMatching(Node followingNode, BasedSequence chars) {
        if (followingNode instanceof Heading) {
            Node child = followingNode.getFirstChild();
            if (child instanceof Strikethrough) {
                Strikethrough strikethrough = (Strikethrough) child;
                return (strikethrough.getChildChars() != null && strikethrough.getChildChars().equals(chars));
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

            addNodes(Heading.class);
        }

        @Override
        public NodePostProcessor apply(Document document) {
            return new ConcordionExampleNodePostProcessor(document);
        }
    }
}