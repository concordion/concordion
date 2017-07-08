package test.concordion.extension;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

import spec.concordion.extension.JavaScriptExtensionTest;

public class JavaScriptEmbeddedExtension implements ConcordionExtension {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withEmbeddedJavaScript(JavaScriptExtensionTest.TEST_JS);
    }
}
