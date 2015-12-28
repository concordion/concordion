package org.concordion.internal.parser.markdown;

import org.parboiled.Parboiled;
import org.pegdown.*;
import org.pegdown.ast.RootNode;

public class MarkdownParser {
    private int pegdownExtensions;

    public MarkdownParser() {
        this(0);
    }
    
    public MarkdownParser(int pegdownExtensions) {
        this.pegdownExtensions = pegdownExtensions;
    }

    public String markdownToHtml(String markdown, String concordionNamespacePrefix) {
        Parser parser = Parboiled.createParser(Parser.class, Extensions.TABLES | Extensions.STRIKETHROUGH | pegdownExtensions, 5000L, Parser.DefaultParseRunnerProvider);
        PegDownProcessor processor = new PegDownProcessor();
        RootNode root = parser.parse(processor.prepareSource(markdown.toCharArray()));
        ToHtmlSerializer serializer = new ConcordionHtmlSerializer(concordionNamespacePrefix);
        return serializer.toHtml(root);
    }
}
