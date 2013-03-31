package test.concordion.extension;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

import spec.concordion.extension.JavaScriptExtensionTest;

public class JavaScriptLinkedExtension implements ConcordionExtension {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withLinkedJavaScript(JavaScriptExtensionTest.SOURCE_PATH, new Resource("/js/my.js"));
    }
}
