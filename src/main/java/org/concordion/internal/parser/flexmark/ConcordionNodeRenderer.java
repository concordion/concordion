package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.options.DataHolder;
import org.concordion.internal.parser.support.Attribute;

import java.util.HashSet;
import java.util.Set;

public class ConcordionNodeRenderer implements NodeRenderer {

    public ConcordionNodeRenderer(DataHolder options) {
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
        set.add(new NodeRenderingHandler<ConcordionExampleNode>(ConcordionExampleNode.class, new CustomNodeRenderer<ConcordionExampleNode>() {
            @Override
            public void render(ConcordionExampleNode node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html, node.getCommand(), node.getExpression());
            }
        }));
        set.add(new NodeRenderingHandler<ConcordionLinkNode>(ConcordionLinkNode.class, new CustomNodeRenderer<ConcordionLinkNode>() {
            @Override
            public void render(ConcordionLinkNode node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html);
            }
        }));

        return set;
    }

    private void renderCommand(ConcordionLinkNode node, NodeRendererContext context, HtmlWriter html) {
        html.attr("href", node.getUrl());
        html.attr(node.getCommand(), node.getRunner());
        html.withAttr().tag("a");
        context.renderChildren(node);
        html.tag("/a");
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

    private void renderCommand(ConcordionExampleNode node, NodeRendererContext context, HtmlWriter html, String command, String expression) {
        // TODO extract method
        final Attributes nodeAttributes = new Attributes();
        nodeAttributes.addValue(command, expression);
        for (Attribute attribute : node.getAttributes()) {
            nodeAttributes.addValue(attribute.name, attribute.value);
        }
        html.setAttributes(nodeAttributes).withAttr().tag("div");
        html.text(node.getChars());
        context.renderChildren(node);
        html.closeTag("div");
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer create(final DataHolder options) {
            return new ConcordionNodeRenderer(options);
        }
    }
}
