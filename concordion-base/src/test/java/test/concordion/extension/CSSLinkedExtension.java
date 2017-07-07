package test.concordion.extension;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

import spec.concordion.extension.CSSExtensionTest;

public class CSSLinkedExtension implements ConcordionExtension {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withLinkedCSS(CSSExtensionTest.SOURCE_PATH, new Resource("/css/my.css"));
    }
}
