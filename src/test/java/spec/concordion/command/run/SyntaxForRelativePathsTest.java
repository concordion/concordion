package spec.concordion.command.run;

import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class SyntaxForRelativePathsTest{

    public String getPath(String resourcePath, String relativePath) {
        return new Resource(resourcePath).getRelativeResource(relativePath).getPath();
    }
}
