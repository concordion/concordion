package spec.concordion.specificationType.markdown;

import java.util.HashMap;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.parser.flexmark.MarkdownToHtml;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class MarkdownGrammarFixture {
    MarkdownToHtml markdownParser;

    public MarkdownGrammarFixture() {
        HashMap<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("ext", "urn:concordion-extensions:2010");
        markdownParser = new MarkdownToHtml(0, namespaces, "concordion");
    }

    public String translate(String markdown) {
        String html = markdownParser.markdownToHtml(markdown);
        if (html.startsWith("<p>") && html.endsWith("</p>")) {
            html = html.substring(3, html.length()-4);
        }
        return html;
    }

    public String foo() {
        return "foo";
    }
}
