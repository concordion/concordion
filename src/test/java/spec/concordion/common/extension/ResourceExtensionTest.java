package spec.concordion.common.extension;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;
import test.concordion.extension.DynamicResourceExtension;
import test.concordion.extension.ResourceExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class ResourceExtensionTest extends AbstractExtensionTestCase {

    public void addResourceExtension() {
        setExtension(new ResourceExtension());
    }

    public void addDynamicResourceExtension() {
        setExtension(new DynamicResourceExtension());
    }

    @Override
    protected void configureTestRig(TestRig testRig) {
        testRig.withResource(new Resource(ResourceExtension.SOURCE_PATH), "0101");
    }
}
