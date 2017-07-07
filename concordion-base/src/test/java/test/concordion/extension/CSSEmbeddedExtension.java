package test.concordion.extension;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

import spec.concordion.extension.CSSExtensionTest;

public class CSSEmbeddedExtension implements ConcordionExtension {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withEmbeddedCSS(CSSExtensionTest.TEST_CSS);
    }
}
