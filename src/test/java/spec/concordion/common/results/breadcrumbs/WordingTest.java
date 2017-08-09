package spec.concordion.common.results.breadcrumbs;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;


@RunWith(ConcordionRunner.class)
public class WordingTest extends AbstractBreadcrumbsTestCase {

    public String getBreadcrumbWordingFor(String resourceName, String content) throws Exception {
        String packageName = "/" + resourceName.replaceAll("\\.html$", "") + "/";
        String otherResourceName = "Demo.html";
        setUpResource(packageName + resourceName, content);
        setUpResource(packageName + otherResourceName, "<html />");
        return getBreadcrumbsFor(packageName + otherResourceName).text.replaceAll(" *> *", "");
    }
}