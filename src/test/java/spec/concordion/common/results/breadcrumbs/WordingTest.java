package spec.concordion.common.results.breadcrumbs;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class WordingTest extends AbstractBreadcrumbsTestCase {

    public String getBreadcrumbWordingFor(String resourceName, String content) throws Exception {
        String packageName = "/" + resourceName.replaceAll("\\.html$", "") + "/";
        String otherResourceName = "Demo.html";
        setUpResource(packageName + resourceName, content);
        setUpResource(packageName + otherResourceName, "<html />");
        return getBreadcrumbsFor(packageName + otherResourceName).text.replaceAll(" *> *", "");
    }
}