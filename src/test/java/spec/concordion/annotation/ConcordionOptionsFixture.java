package spec.concordion.annotation;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ConcordionOptionsParser;
import org.concordion.internal.ConfigurationException;
import org.concordion.internal.parser.markdown.MarkdownParser;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class ConcordionOptionsFixture {
    private int pegdownExtensions;
    
    public String translate(String markdown) {
        MarkdownParser markdownParser = new MarkdownParser(pegdownExtensions, Collections.<String, String> emptyMap());
        String html = markdownParser.markdownToHtml(markdown, "concordion");
        if (html.startsWith("<p>") && html.endsWith("</p>")) {
            html = html.substring(3, html.length()-4);
        }
        return html;
    }
    
    public void withWikilinkAndAutolink() {
        pegdownExtensions = org.pegdown.Extensions.WIKILINKS | org.pegdown.Extensions.AUTOLINKS;
    }
    
    public String parse(String declareNamespaces) {
        String[] namespacePairs = declareNamespaces.substring(1, declareNamespaces.length()-2).split(",");
        Map<String, String> namespaceMap = ConcordionOptionsParser.convertNamespacePairsToMap(namespacePairs);
        return new TreeMap<String, String>(namespaceMap).toString();
    }

    public String parseAndReturnExceptionMessage(String declareNamespaces) {
        try {
            parse(declareNamespaces);
            return "";
        } catch (ConfigurationException e) {
            return e.getMessage();
        }
    }
}
