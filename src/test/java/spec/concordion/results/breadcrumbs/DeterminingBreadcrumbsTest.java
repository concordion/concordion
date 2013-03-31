package spec.concordion.results.breadcrumbs;


public class DeterminingBreadcrumbsTest extends AbstractBreadcrumbsTestCase {

    public String getBreadcrumbTextFor(String resourceName) throws Exception {
        return getBreadcrumbsFor(resourceName).text;
    }
    
    public void setUpResource(String resourceName) {
        setUpResource(resourceName, "<html />");
    }
}