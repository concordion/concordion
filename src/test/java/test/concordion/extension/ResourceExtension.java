package test.concordion.extension;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class ResourceExtension implements ConcordionExtension {

    public static final String SOURCE_PATH = "/test/concordion/o.png";

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withResource(SOURCE_PATH, at("/images/o.png"));
    }

    private Resource at(String path) {
        return new Resource(path);
    }
}
