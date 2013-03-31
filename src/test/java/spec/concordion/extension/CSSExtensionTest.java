package spec.concordion.extension;

import org.concordion.api.Resource;

import test.concordion.TestRig;
import test.concordion.extension.CSSEmbeddedExtension;
import test.concordion.extension.CSSLinkedExtension;

public class CSSExtensionTest extends AbstractExtensionTestCase {

    public static final String SOURCE_PATH = "/test/concordion/my.css";
    public static final String TEST_CSS = "/* My test CSS */";

    public void addLinkedCSSExtension() {
        setExtension(new CSSLinkedExtension());
    }

    public void addEmbeddedCSSExtension() {
        setExtension(new CSSEmbeddedExtension());
    }

    @Override
    protected void configureTestRig(TestRig testRig) {
        testRig.withResource(new Resource(SOURCE_PATH), TEST_CSS);
    }
    
    public boolean hasCSSDeclaration(String cssFilename) {
        return getProcessingResult().hasCSSDeclaration(cssFilename);
    }

    public boolean hasEmbeddedTestCSS() {
        return getProcessingResult().hasEmbeddedCSS(TEST_CSS);
    }
}
