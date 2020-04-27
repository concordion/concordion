package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import org.concordion.internal.parser.support.Attribute;

import java.util.HashSet;
import java.util.Set;

public class ConcordionTableRenderer implements NodeRenderer {
    private final ConcordionTableOptions options;

    public ConcordionTableRenderer(DataHolder options) {
        this.options = new ConcordionTableOptions(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<ConcordionTableBlock>(ConcordionTableBlock.class, new NodeRenderingHandler.CustomNodeRenderer<ConcordionTableBlock>() {
            @Override
            public void render(ConcordionTableBlock node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html);
            }
        }));
        set.add(new NodeRenderingHandler<ConcordionTableCell>(ConcordionTableCell.class, new NodeRenderingHandler.CustomNodeRenderer<ConcordionTableCell>() {
            @Override
            public void render(ConcordionTableCell node, NodeRendererContext context, HtmlWriter html) {
                renderCommand(node, context, html);
            }
        }));

        return set;
    }

    private void renderCommand(final ConcordionTableBlock node, final NodeRendererContext context, HtmlWriter html) {
        if (!options.className.isEmpty()) {
            html.attr("class", options.className);
        }

        if (node.getCommand() != null) {
            html.attr(node.getCommand(), node.getExpression());
        }

        for (Attribute attribute : node.getAttributes()) {
            html.attr(attribute.name, attribute.value);
        }

        html.srcPosWithEOL(node.getChars()).withAttr().tagLineIndent("table", new Runnable() {
            @Override
            public void run() {
                context.renderChildren(node);
            }
        });
    }

    private void renderCommand(final ConcordionTableCell node, final NodeRendererContext context, HtmlWriter html) {
        String tag = node.isHeader() ? "th" : "td";
        if (node.getAlignment() != null) {
            html.attr("align", getAlignValue(node.getAlignment()));
        }

        if (options.columnSpans && node.getSpan() > 1) {
            html.attr("colspan", String.valueOf(node.getSpan()));
        }

        if (node.getCommand() != null) {
            html.attr(node.getCommand(), node.getExpression());
        }

        html.srcPos(node.getText()).withAttr().tag(tag);
        context.renderChildren(node);
        html.tag("/" + tag);
    }

    private static String getAlignValue(TableCell.Alignment alignment) {
        switch (alignment) {
            case LEFT:
                return "left";
            case CENTER:
                return "center";
            case RIGHT:
                return "right";
        }
        throw new IllegalStateException("Unknown alignment: " + alignment);
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new ConcordionTableRenderer(options);
        }
    }
}
