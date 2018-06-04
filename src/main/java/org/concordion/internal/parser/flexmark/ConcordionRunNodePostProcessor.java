package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.options.DataHolder;

public class ConcordionRunNodePostProcessor extends NodePostProcessor {
    private final String TARGET_CONCORDION_NAMESPACE_PREFIX;
    private final String SOURCE_RUN_COMMAND;
    private final String TARGET_RUN_COMMAND;

    public ConcordionRunNodePostProcessor(DataHolder options) {
        TARGET_CONCORDION_NAMESPACE_PREFIX = ConcordionMarkdownOptions.CONCORDION_TARGET_NAMESPACE.getFrom(options);
        TARGET_RUN_COMMAND = TARGET_CONCORDION_NAMESPACE_PREFIX + ":run";
        SOURCE_RUN_COMMAND = ConcordionMarkdownOptions.SOURCE_CONCORDION_NAMESPACE_PREFIX + ":run";
    }

    @Override
    public void process(NodeTracker state, Node node) {
        visit(state, (Link) node);
    }

    private void visit(NodeTracker state, Link linkNode) {
        String title = linkNode.getTitle().toString();

        if (title.startsWith(SOURCE_RUN_COMMAND)) {
            String suffix = title.substring(SOURCE_RUN_COMMAND.length()).trim();
            String runner;
            if (suffix.startsWith("=")) {
                runner = suffix.substring(1).trim();
                if (runner.startsWith("'") || runner.startsWith("\"")) {
                    runner = runner.substring(1, runner.length() - 1);
                }
            } else {
                runner = "concordion";
            }

            ConcordionRunNode newLinkNode = new ConcordionRunNode();
            newLinkNode.setRunner(runner);
            newLinkNode.setCommand(TARGET_RUN_COMMAND);
            newLinkNode.setUrl(linkNode.getUrl());
            newLinkNode.setChars(linkNode.getChars());
            Node firstChild = linkNode.getFirstChild();
            if (firstChild != null) {
                newLinkNode.appendChild(firstChild);
            }
            replaceNode(state, linkNode, newLinkNode);
        }
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
        }

        @Override
        public NodePostProcessor create(Document document) {
            return new ConcordionRunNodePostProcessor(document);
        }
    }
}