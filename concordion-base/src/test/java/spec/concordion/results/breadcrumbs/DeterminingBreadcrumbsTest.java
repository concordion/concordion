package spec.concordion.results.breadcrumbs;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;


@RunWith(ConcordionRunner.class)
public class DeterminingBreadcrumbsTest extends AbstractBreadcrumbsTestCase {

    public String getBreadcrumbTextFor(String resourceName) throws Exception {
        return getBreadcrumbsFor(resourceName).text;
    }
    
    public void setUpResource(String resourceName) {
        setUpResource(resourceName, "<html />");
    }
}