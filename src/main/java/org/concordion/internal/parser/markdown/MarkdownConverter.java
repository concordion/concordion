package org.concordion.internal.parser.markdown;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.concordion.api.SpecificationConverter;

public class MarkdownConverter implements SpecificationConverter {

    public static final SpecificationConverter INSTANCE = new MarkdownConverter();
    private static final String CONCORDION_NAMESPACE_PREFIX = "concordion";
    private int pegdownExtensions;

    @Override
    public InputStream convert(InputStream inputStream) throws IOException {
        String markdown = asString(inputStream);
        MarkdownParser markdownParser = new MarkdownParser(pegdownExtensions);
        String html = markdownParser.markdownToHtml(markdown, CONCORDION_NAMESPACE_PREFIX);
        html = wrapBody(html);

        return new ByteArrayInputStream(html.getBytes());
    }

    private String asString(InputStream inputStream) {
        String markdown;
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputStream, "UTF-8");
            markdown = scanner.useDelimiter("\\A").next();
        } finally {
            scanner.close();
        }
        return markdown;
    }

    private String wrapBody(String body) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                + "<html xmlns:" + CONCORDION_NAMESPACE_PREFIX + "='http://www.concordion.org/2007/concordion'><body>" + body + "</body></html>";
    }

    public void withPegdownExtensions(int extensions) {
        this.pegdownExtensions = extensions;
    }
}
