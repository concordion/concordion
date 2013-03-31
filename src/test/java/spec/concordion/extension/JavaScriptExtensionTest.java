package spec.concordion.extension;

import org.concordion.api.Resource;

import test.concordion.TestRig;
import test.concordion.extension.JavaScriptEmbeddedExtension;
import test.concordion.extension.JavaScriptLinkedExtension;

public class JavaScriptExtensionTest extends AbstractExtensionTestCase {

    public static final String SOURCE_PATH = "/test/concordion/my.js";
    public static final String TEST_JS = "/* My test JS */";

    public void addLinkedJavaScriptExtension() {
        setExtension(new JavaScriptLinkedExtension());
    }

    public void addEmbeddedJavaScriptExtension() {
        setExtension(new JavaScriptEmbeddedExtension());
    }

    @Override
    protected void configureTestRig(TestRig testRig) {
        testRig.withResource(new Resource(SOURCE_PATH), TEST_JS);
    }
    
    public boolean hasJavaScriptDeclaration(String cssFilename) {
        return getProcessingResult().hasJavaScriptDeclaration(cssFilename);
    }

    public boolean hasEmbeddedTestJavaScript() {
        return getProcessingResult().hasEmbeddedJavaScript(TEST_JS);
    }
}
