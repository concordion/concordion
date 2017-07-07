package spec.concordion.results.breadcrumbs;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class BreadcrumbsTest extends AbstractBreadcrumbsTestCase {

    public void setUpResource(String resourceName, String content) {
        super.setUpResource(resourceName, content);
    }
    
    public Result getBreadcrumbsFor(String resourceName) throws Exception {
        return super.getBreadcrumbsFor(resourceName);
    }
}