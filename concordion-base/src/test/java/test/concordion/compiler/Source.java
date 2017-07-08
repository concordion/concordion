package test.concordion.compiler;

import java.net.URI;
import java.net.URISyntaxException;

public class Source {

    private final String content;
    private final URI path;

    public Source(String content, String path) {
        this(content, toURI(path));
    }

    public Source(String content, URI path) {
        this.content = content;
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public URI getPath() {
        return path;
    }
    
    public static URI toURI(String path) {
        try {
            return new URI(path);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert path '" + path + "' to URI.", e);
        }
    }
}
