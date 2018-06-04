package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.ReferenceRepository;
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
            BasedSequence title = linkNode.getTitle();
            BasedSequence text = linkNode.getText();
            if (isExpressionOnlyCommand(linkNode)) {
                text = BasedSequence.NULL;
            }
            ConcordionCommandNode commandNode = createConcordionCommandNode(title, text);
            replaceNode(state, linkNode, commandNode);
        }
    }

    private void visit(NodeTracker state, LinkRef linkRefNode) {
        Reference referenceNode = linkRefNode.getReferenceNode(referenceRepository);
        if (referenceNode.getUrl().equals(ConcordionMarkdownOptions.URL_FOR_CONCORDION)) {
            BasedSequence title = referenceNode.getTitle();
            BasedSequence text = linkRefNode.getText();
            if (isExpressionOnlyCommand(linkRefNode)) {
                text = BasedSequence.NULL;
            }
            // Implicit links leave the text empty to derive the text from the reference. See https://daringfireball.net/projects/markdown/syntax#link.
            if (text.isEmpty()) {
                text = linkRefNode.getReference();
            }
            ConcordionCommandNode commandNode = createConcordionCommandNode(title, text);
            replaceNode(state, linkRefNode, commandNode);
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