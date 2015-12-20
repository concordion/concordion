package spec.concordion.specificationType.markdown;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.parser.markdown.MarkdownParser;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class MarkdownGrammarFixture {
    MarkdownParser markdownParser = new MarkdownParser();

    public String translate(String markdown) {
        String html = markdownParser.markdownToHtml(markdown, "concordion");
        if (html.startsWith("<p>") && html.endsWith("</p>")) {
            html = html.substring(3, html.length()-4);
        }
        return html;
    }
}
