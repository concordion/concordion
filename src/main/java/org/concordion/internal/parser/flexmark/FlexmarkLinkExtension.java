package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.youtube.embedded.internal.YouTubeLinkNodePostProcessor;
import com.vladsch.flexmark.ext.youtube.embedded.internal.YouTubeLinkNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.youtrack.converter.internal.YouTrackConverterNodeRenderer;

public class FlexmarkLinkExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    private FlexmarkLinkExtension() {
    }

    public static Extension create() {
        return new FlexmarkLinkExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new ConcordionNodePostProcessor.Factory(parserBuilder));
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
            rendererBuilder.nodeRendererFactory(new ConcordionNodeRenderer.Factory());
        }
    }
}