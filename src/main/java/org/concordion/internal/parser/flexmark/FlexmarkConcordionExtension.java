package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.misc.Extension;

public class FlexmarkConcordionExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    private FlexmarkConcordionExtension() {
    }

    public static Extension create() {
        return new FlexmarkConcordionExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new ConcordionExampleNodePostProcessor.Factory(parserBuilder));
        parserBuilder.postProcessorFactory(new ConcordionRunNodePostProcessor.Factory(parserBuilder));
        parserBuilder.postProcessorFactory(new ConcordionCommandNodePostProcessor.Factory(parserBuilder));
    }

    @Override
    public void rendererOptions(final MutableDataHolder options) {
    }

    @Override
    public void parserOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        if (rendererType.equals("HTML")) {
            rendererBuilder.nodeRendererFactory(new ConcordionCommandNodeRenderer.Factory());
            rendererBuilder.nodeRendererFactory(new ConcordionExampleNodeRenderer.Factory());
            rendererBuilder.nodeRendererFactory(new ConcordionRunNodeRenderer.Factory());
            rendererBuilder.nodeRendererFactory(new ConcordionTableRenderer.Factory());
        }
    }
}