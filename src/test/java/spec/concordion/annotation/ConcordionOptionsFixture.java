package spec.concordion.annotation;

import com.vladsch.flexmark.profile.pegdown.Extensions;
import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ConcordionOptionsParser;
import org.concordion.internal.ConfigurationException;
import org.concordion.internal.parser.flexmark.FlexmarkMarkdownTranslator;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class ConcordionOptionsFixture {
    private int pegdownExtensions;

    public String translate(String markdown) {
        FlexmarkMarkdownTranslator markdownParser = new FlexmarkMarkdownTranslator(pegdownExtensions, null, Collections.<String, String> emptyMap(), "concordion");
        String html = markdownParser.markdownToHtml(markdown);
        if (html.startsWith("<p>") && html.endsWith("</p>")) {
            html = html.substring(3, html.length()-4);
        }
        return html;
    }
    
    public void withWikilinkAndAutolink() {
        pegdownExtensions = Extensions.WIKILINKS | Extensions.AUTOLINKS;
    }

    public String parse(String declareNamespaces) {
        declareNamespaces = declareNamespaces.trim();
        String[] namespacePairs = declareNamespaces.substring(1, declareNamespaces.length()-1).split(",");
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
