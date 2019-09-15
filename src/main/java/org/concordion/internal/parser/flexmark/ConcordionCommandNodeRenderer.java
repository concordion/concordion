package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.data.DataHolder;
import org.concordion.internal.parser.support.Attribute;

import java.util.HashSet;
import java.util.Set;

public class ConcordionCommandNodeRenderer implements NodeRenderer {

    public ConcordionCommandNodeRenderer(DataHolder options) {
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<ConcordionCommandNode>(ConcordionCommandNode.class, new CustomNodeRenderer<ConcordionCommandNode>() {
            @Override
            public void render(ConcordionCommandNode node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html, node.getCommand(), node.getExpression());
            }
        }));
        return set;
    }

    private void renderCommand(ConcordionCommandNode node, NodeRendererContext context, HtmlWriter html, String command, String expression) {
        final Attributes nodeAttributes = new Attributes();
        nodeAttributes.addValue(command, expression);
        for (Attribute attribute : node.getAttributes()) {
            nodeAttributes.addValue(attribute.name, attribute.value);
        }
        html.setAttributes(nodeAttributes).withAttr().tag("span");
        html.text(node.getChars());
        context.delegateRender();
        html.closeTag("span");
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new ConcordionCommandNodeRenderer(options);
        }
    }
}
