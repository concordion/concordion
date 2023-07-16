package spec.concordion.common.results.breadcrumbs;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class DeterminingBreadcrumbsTest extends AbstractBreadcrumbsTestCase {

    public String getBreadcrumbTextFor(String resourceName) throws Exception {
        return getBreadcrumbsFor(resourceName).text;
    }
    
    public void setUpResource(String resourceName) {
        setUpResource(resourceName, "<html />");
    }
}