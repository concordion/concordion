package spec.concordion.common.results.breadcrumbs;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class BreadcrumbsTest extends AbstractBreadcrumbsTestCase {

    public void setUpResource(String resourceName, String content) {
        super.setUpResource(resourceName, content);
    }
    
    public Result getBreadcrumbsFor(String resourceName) throws Exception {
        return super.getBreadcrumbsFor(resourceName);
    }
}