package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.Collections;
import java.util.Map;

public class FlexmarkParser {
    private final DataHolder options;
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;
    private Map<String, String> namespaces;


    public FlexmarkParser() {
        this(0, Collections.<String, String> emptyMap());
    }
    
    public FlexmarkParser(int pegdownExtensions, Map<String, String> namespaces) {
        this.namespaces = namespaces;
        options = PegdownOptionsAdapter.flexmarkOptions(true,
                Extensions.TABLES | Extensions.STRIKETHROUGH | pegdownExtensions,
                FlexmarkLinkExtension.create());

        parser = Parser.builder(options).build();
        htmlRenderer = HtmlRenderer.builder(options).build();
    }

    public String markdownToHtml(String markdown, String concordionNamespacePrefix) {
        Node document = parser.parse(markdown);
        String html = htmlRenderer.render(document);
        return html;
    }
}
