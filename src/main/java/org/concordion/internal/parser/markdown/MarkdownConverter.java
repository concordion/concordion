package org.concordion.internal.parser.markdown;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.concordion.api.Resource;
import org.concordion.api.SpecificationConverter;
import org.concordion.api.Target;
import org.concordion.internal.util.IOUtil;

public class MarkdownConverter implements SpecificationConverter {

    public static final SpecificationConverter INSTANCE = new MarkdownConverter();
    private static final String CONCORDION_NAMESPACE_PREFIX = "concordion";
    private Target sourceHtmlTarget;
    private int pegdownExtensions;
    

    @Override
    public InputStream convert(Resource resource, InputStream inputStream) throws IOException {
        String markdown = IOUtil.readResourceAsString(resource.getPath());
        MarkdownParser markdownParser = new MarkdownParser(pegdownExtensions);
        String html = markdownParser.markdownToHtml(markdown, CONCORDION_NAMESPACE_PREFIX);
        html = wrapBody(html);
        if (sourceHtmlTarget != null) {
            Resource sourceHtmlResource = new Resource(resource.getPath() + ".html");
            sourceHtmlTarget.write(sourceHtmlResource, html);
            System.out.println(String.format("[Source: %s]", sourceHtmlTarget.resolvedPathFor(sourceHtmlResource)));
        }

        return new ByteArrayInputStream(html.getBytes());
    }

    private String wrapBody(String body) {
        return "<html xmlns:" + CONCORDION_NAMESPACE_PREFIX + "='http://www.concordion.org/2007/concordion'><body>" + body + "</body></html>";
    }

    public void setSourceHtmlTarget(Target target) {
        this.sourceHtmlTarget = target;
    }

    public void withPegdownExtensions(int extensions) {
        this.pegdownExtensions = extensions;
    }
}
