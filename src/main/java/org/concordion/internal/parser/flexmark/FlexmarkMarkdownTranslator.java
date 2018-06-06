package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Map;

public class FlexmarkMarkdownTranslator {
    private final MutableDataHolder options;
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    // TODO allow other extensions to be added
    public FlexmarkMarkdownTranslator(int pegdownExtensions, Map<String, String> namespaces, String targetConcordionNamespace) {

        // Only interrupts an HTML block on a blank line if all tags in the HTML block are closed.
        // Closer to Pegdown HTML block parsing behaviour.
        // TODO - allow this to be overridden.
        boolean strictHtml = false;

        options = new MutableDataSet(PegdownOptionsAdapter.flexmarkOptions(strictHtml,
                Extensions.TABLES | Extensions.STRIKETHROUGH | pegdownExtensions,
                FlexmarkConcordionExtension.create()));
        options.set(ConcordionMarkdownOptions.CONCORDION_ADDITIONAL_NAMESPACES, namespaces);
        options.set(ConcordionMarkdownOptions.CONCORDION_TARGET_NAMESPACE, targetConcordionNamespace);

        parser = Parser.builder(options).build();
        htmlRenderer = HtmlRenderer.builder(options).build();
    }

    public String markdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        String html = htmlRenderer.render(document);
        return html.trim();
    }
}
