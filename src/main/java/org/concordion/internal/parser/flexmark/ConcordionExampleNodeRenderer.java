package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import org.concordion.internal.parser.support.Attribute;

import java.util.HashSet;
import java.util.Set;

public class ConcordionExampleNodeRenderer implements NodeRenderer {

    public ConcordionExampleNodeRenderer(DataHolder options) {
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<ConcordionExampleNode>(ConcordionExampleNode.class, new CustomNodeRenderer<ConcordionExampleNode>() {
            @Override
            public void render(ConcordionExampleNode node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html, node.getCommand(), node.getExpression());
            }
        }));

        return set;
    }

    private void renderCommand(ConcordionExampleNode node, NodeRendererContext context, HtmlWriter html, String command, String expression) {
        html.attr(command, expression);
        for (Attribute attribute : node.getAttributes()) {
            html.attr(attribute.name, attribute.value);
        }
        html.withAttr().tag("div");
        html.text(node.getChars());
        context.renderChildren(node);
        html.closeTag("div");
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new ConcordionExampleNodeRenderer(options);
        }
    }
}
