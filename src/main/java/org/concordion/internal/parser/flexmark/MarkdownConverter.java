package org.concordion.internal.parser.flexmark;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import com.vladsch.flexmark.util.data.DataSet;
import org.concordion.api.SpecificationConverter;
import org.concordion.internal.parser.flexmark.FlexmarkMarkdownTranslator;

public class MarkdownConverter implements SpecificationConverter {

    private static final String CONCORDION_NAMESPACE_PREFIX = "concordion";
    private Map<String, String> namespaces = Collections.emptyMap();
    private int pegdownExtensions;
    private DataSet flexmarkOptions;

    @Override
    public InputStream convert(InputStream inputStream, String specificationName) throws IOException {
        String markdown = asString(inputStream);
        FlexmarkMarkdownTranslator markdownParser = new FlexmarkMarkdownTranslator(pegdownExtensions, flexmarkOptions, namespaces, CONCORDION_NAMESPACE_PREFIX);
        String html = markdownParser.markdownToHtml(markdown);
        html = wrapBody(html);

        return new ByteArrayInputStream(html.getBytes("UTF-8"));
    }

    private String asString(InputStream inputStream) {
        String markdown;
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputStream, "UTF-8");
            markdown = scanner.useDelimiter("\\A").next();
        } catch (NoSuchElementException e) { // empty spec
            markdown = "";
        } finally {
            scanner.close();
        }
        return markdown;
    }

    private String wrapBody(String body) {
        String html = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                        + "<html"
                        + " xmlns:" + CONCORDION_NAMESPACE_PREFIX + "='http://www.concordion.org/2007/concordion'";
        for (Entry<String, String> namespace : namespaces.entrySet()) {
            html += " xmlns:" + namespace.getKey() + "='" + namespace.getValue() + "'";
        }
        html += "><body>" + body + "</body></html>";
        return html;
    }

    public void withPegdownExtensions(int extensions) {
        this.pegdownExtensions = extensions;
    }

    public void withNamespaceDeclarations(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public void withFlexmarkOptions(DataSet flexmarkOptions) {
        this.flexmarkOptions = flexmarkOptions;
    }
}
