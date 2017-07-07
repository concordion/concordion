package org.concordion.internal.parser.markdown;

import java.util.Collections;
import java.util.Map;

import org.parboiled.Parboiled;
import org.pegdown.Extensions;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;

public class MarkdownParser {
    private int pegdownExtensions;
    private Map<String, String> namespaces;

    public MarkdownParser() {
        this(0, Collections.<String, String> emptyMap());
    }
    
    public MarkdownParser(int pegdownExtensions, Map<String, String> namespaces) {
        this.pegdownExtensions = pegdownExtensions;
        this.namespaces = namespaces;
    }

    public String markdownToHtml(String markdown, String concordionNamespacePrefix) {
        Parser parser = Parboiled.createParser(Parser.class, Extensions.TABLES | Extensions.STRIKETHROUGH | pegdownExtensions, 5000L, Parser.DefaultParseRunnerProvider);
        PegDownProcessor processor = new PegDownProcessor();
        RootNode root = parser.parse(processor.prepareSource(markdown.toCharArray()));
        ToHtmlSerializer serializer = new ConcordionHtmlSerializer(concordionNamespacePrefix, namespaces);
        return serializer.toHtml(root);
    }
}
