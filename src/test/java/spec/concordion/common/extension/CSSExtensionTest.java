package spec.concordion.common.extension;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;
import test.concordion.extension.CSSEmbeddedExtension;
import test.concordion.extension.CSSLinkedExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
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
