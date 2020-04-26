package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.builder.Extension;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlexmarkMarkdownTranslator {
    private final MutableDataHolder options;
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    public FlexmarkMarkdownTranslator(int pegdownExtensions, DataSet flexmarkOptions, Map<String, String> namespaces, String targetConcordionNamespace) {
        // Only interrupts an HTML block on a blank line if all tags in the HTML block are closed.
        // Closer to Pegdown HTML block parsing behaviour.
        boolean strictHtml = false;
        List<Extension> flexmarkExtensionsToAdd = new ArrayList<>();
        flexmarkExtensionsToAdd.add(FlexmarkConcordionExtension.create());

        options = new MutableDataSet(PegdownOptionsAdapter.flexmarkOptions(strictHtml,
                Extensions.TABLES | Extensions.STRIKETHROUGH | pegdownExtensions,
                flexmarkExtensionsToAdd.toArray(new Extension[0])));
        if (flexmarkOptions != null) {
            options.setAll(flexmarkOptions);
        }
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
