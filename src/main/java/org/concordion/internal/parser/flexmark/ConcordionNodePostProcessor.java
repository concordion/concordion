package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.ReferenceRepository;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.concordion.internal.parser.support.ConcordionStatement;

import java.util.Collections;
import java.util.Map;

public class ConcordionNodePostProcessor extends NodePostProcessor {
    public static final String URL_FOR_CONCORDION = "-";
    private static final String SOURCE_CONCORDION_NAMESPACE_PREFIX = "c";
    // TODO allow following to be overridden
    private static final String TARGET_CONCORDION_NAMESPACE_PREFIX = "concordion";
    private Map<String, String> namespaces = Collections.emptyMap();

    private final ReferenceRepository referenceRepository;
    private final ConciseExpressionParser statementParser;

    public ConcordionNodePostProcessor(DataHolder options) {
        this.referenceRepository = options.get(Parser.REFERENCES);
        statementParser = new ConciseExpressionParser(SOURCE_CONCORDION_NAMESPACE_PREFIX, TARGET_CONCORDION_NAMESPACE_PREFIX, namespaces);
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Link) {
            Link linkNode = (Link) node;
            if (linkNode.getUrl().equals(URL_FOR_CONCORDION)) {
                BasedSequence title = linkNode.getTitle();
                BasedSequence text = linkNode.getText();
                if (expressionOnlyCommand(linkNode)) {
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
                if (expressionOnlyCommand(linkRefNode)) {
                    text = BasedSequence.NULL;
                }
                if (text.isEmpty()) {
                    text = referenceNode.getReference();
                }
                ConcordionCommandNode commandNode = createNode(title, text);
                swapNode(state, node, commandNode);
            }
        }
    }

    private boolean expressionOnlyCommand(Node node) {
        return node.getFirstChild() instanceof Emphasis;
    }

    private ConcordionCommandNode createNode(BasedSequence title, BasedSequence text) {
        ConcordionStatement statement = statementParser.parse(title.toString(), text.toString());
        return ConcordionCommandNode.createNode(statement, title, text);
    }

    private void swapNode(NodeTracker state, Node node, ConcordionCommandNode commandNode) {
        node.insertAfter(commandNode);
        node.unlink();
        state.nodeRemoved(node);
        state.nodeAddedWithChildren(commandNode);
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory(DataHolder options) {
            super(false);

            addNodes(Link.class);
            addNodes(LinkRef.class);
        }

        @Override
        public NodePostProcessor create(Document document) {
            return new ConcordionNodePostProcessor(document);
        }
    }
}
