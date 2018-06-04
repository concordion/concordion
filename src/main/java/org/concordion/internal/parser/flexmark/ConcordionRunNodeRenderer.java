package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.HashSet;
import java.util.Set;

public class ConcordionRunNodeRenderer implements NodeRenderer {

    public ConcordionRunNodeRenderer(DataHolder options) {
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<ConcordionRunNode>(ConcordionRunNode.class, new CustomNodeRenderer<ConcordionRunNode>() {
            @Override
            public void render(ConcordionRunNode node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html);
            }
        }));

        return set;
    }

    private void renderCommand(ConcordionRunNode node, NodeRendererContext context, HtmlWriter html) {
        html.attr("href", node.getUrl());
        html.attr(node.getCommand(), node.getRunner());
        html.withAttr().tag("a");
        context.renderChildren(node);
        html.tag("/a");
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer create(final DataHolder options) {
            return new ConcordionRunNodeRenderer(options);
        }
    }
}
