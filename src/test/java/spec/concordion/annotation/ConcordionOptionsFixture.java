package spec.concordion.annotation;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.parser.markdown.MarkdownParser;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class ConcordionOptionsFixture {
    private int pegdownExtensions;
    
    public String translate(String markdown) {
        MarkdownParser markdownParser = new MarkdownParser(pegdownExtensions);
        String html = markdownParser.markdownToHtml(markdown, "concordion");
        if (html.startsWith("<p>") && html.endsWith("</p>")) {
            html = html.substring(3, html.length()-4);
        }
        return html;
    }
    
    public void withWikilinkAndAutolink() {
        pegdownExtensions = org.pegdown.Extensions.WIKILINKS | org.pegdown.Extensions.AUTOLINKS;
    }
}
