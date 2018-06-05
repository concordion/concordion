package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.ReferenceRepository;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.markdown.ConcordionMarkdownException;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.concordion.internal.parser.support.ConcordionStatement;

public class ConcordionCommandNodePostProcessor extends NodePostProcessor {
    private final String TARGET_CONCORDION_NAMESPACE_PREFIX;
    private final ReferenceRepository referenceRepository;
    private final ConciseExpressionParser statementParser;

    public ConcordionCommandNodePostProcessor(DataHolder options) {
        TARGET_CONCORDION_NAMESPACE_PREFIX = ConcordionMarkdownOptions.CONCORDION_TARGET_NAMESPACE.getFrom(options);
        this.referenceRepository = options.get(Parser.REFERENCES);
        statementParser = new ConciseExpressionParser(ConcordionMarkdownOptions.SOURCE_CONCORDION_NAMESPACE_PREFIX,
                TARGET_CONCORDION_NAMESPACE_PREFIX,
                ConcordionMarkdownOptions.CONCORDION_ADDITIONAL_NAMESPACES.getFrom(options));
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Link) {
            visit(state, (Link) node);
        } else if (node instanceof LinkRef) {
            visit(state, (LinkRef) node);
        }
    }

    private void visit(NodeTracker state, Link linkNode) {
        if (linkNode.getUrl().equals(ConcordionMarkdownOptions.URL_FOR_CONCORDION)) {
            processNode(state, linkNode, linkNode.getText(), linkNode.getTitle());
        }
    }

    private void visit(NodeTracker state, LinkRef linkRefNode) {
        Reference referenceNode = linkRefNode.getReferenceNode(referenceRepository);
        if (referenceNode.getUrl().equals(ConcordionMarkdownOptions.URL_FOR_CONCORDION)) {
            BasedSequence text = linkRefNode.getText();
            // Implicit links leave the text empty to derive the text from the reference. See https://daringfireball.net/projects/markdown/syntax#link.
            if (text.isEmpty()) {
                text = linkRefNode.getReference();
            }
            processNode(state, linkRefNode, text, referenceNode.getTitle());
        }
    }

    private void processNode(NodeTracker state, Node node, BasedSequence text, BasedSequence title) {
        if (isExpressionOnlyCommand(node)) {
            text = BasedSequence.NULL;
        }
        ConcordionCommandNode commandNode = createConcordionCommandNode(title, text);
        if (node.getParent() instanceof TableCell) {
            if (insideConcordionTableBlock(node)) {
                changeToConcordionTableCell(state, node, commandNode);
            } else {
                changeToConcordionTableBlock(state, node, commandNode);
            }
        } else {
            replaceNode(state, node, commandNode);
        }
    }

    /**
     * <b>Expression-only commands</b>.
     *
     * For Concordion, some commands only require an expression and don't need a text value to be passed. However, Markdown links always require link text.
     * Any text value that starts with italics will be set to an empty text value in the output specification.
     */
    private boolean isExpressionOnlyCommand(Node node) {
        return node.getFirstChild() instanceof Emphasis;
    }

    private ConcordionCommandNode createConcordionCommandNode(BasedSequence title, BasedSequence text) {
        if (title.trim().startsWith("c:run")) {
            throw new ConcordionMarkdownException("Markdown link contains invalid URL for \"c:run\" command.\n"
                    + "Set the URL to the location of the specification to be run, rather than '-'.\n"
                    + "For example, [My Specification](mySpec.md \"c:run\")");
        }

        ConcordionStatement statement = statementParser.parse(title.toString(), text.toString());
        return ConcordionCommandNode.createNode(statement, text);
    }

    private boolean insideConcordionTableBlock(Node node) {
        while (node != null && !(node instanceof TableBlock) && !(node instanceof ConcordionTableBlock)) {
            node = node.getParent();
        }
        return node instanceof ConcordionTableBlock;
    }

    private void changeToConcordionTableBlock(NodeTracker state, Node linkNode, ConcordionCommandNode commandNode) {
        Node tableBlock = linkNode;
        while (!(tableBlock instanceof TableBlock)) {
            tableBlock = tableBlock.getParent();
        }
        ConcordionTableBlock concordionTableBlock = new ConcordionTableBlock(tableBlock.getChars(), commandNode);
        tableBlock.insertBefore(concordionTableBlock);
        concordionTableBlock.takeChildren(tableBlock);
        tableBlock.unlink();
        linkNode.unlink();
        state.nodeAddedWithDescendants(concordionTableBlock);
        state.nodeRemoved(tableBlock);
        state.nodeRemoved(linkNode);
    }

    private void changeToConcordionTableCell(NodeTracker state, Node linkNode, ConcordionCommandNode commandNode) {
        Node tableCell = linkNode;
        while (!(tableCell instanceof TableCell)) {
            tableCell = tableCell.getParent();
        }
        ConcordionTableCell concordionTableCell = new ConcordionTableCell(tableCell.getChars(), commandNode);
        concordionTableCell.setAlignment(((TableCell) tableCell).getAlignment());
        concordionTableCell.setHeader(((TableCell) tableCell).isHeader());
        concordionTableCell.setSpan(((TableCell) tableCell).getSpan());
        concordionTableCell.appendChild(linkNode.getFirstChild());
        tableCell.insertBefore(concordionTableCell);
        tableCell.unlink();
        linkNode.unlink();
        state.nodeRemoved(tableCell);
        state.nodeRemoved(linkNode);
    }

    private void replaceNode(NodeTracker state, Node toBeRemoved, Node toBeReplacedBy) {
        toBeRemoved.insertBefore(toBeReplacedBy);
        toBeRemoved.unlink();
        state.nodeAddedWithDescendants(toBeReplacedBy);
        state.nodeRemoved(toBeRemoved);
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory(DataHolder options) {
            super(false);

            addNodes(Link.class);
            addNodes(LinkRef.class);
        }

        @Override
        public NodePostProcessor create(Document document) {
            return new ConcordionCommandNodePostProcessor(document);
        }
    }
}