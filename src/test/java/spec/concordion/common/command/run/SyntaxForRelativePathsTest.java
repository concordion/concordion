package spec.concordion.common.command.run;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class SyntaxForRelativePathsTest{

    public String getPath(String resourcePath, String relativePath) {
        return new Resource(resourcePath).getRelativeResource(relativePath).getPath();
    }
}
