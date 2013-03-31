package spec.concordion.command.run;

import org.concordion.api.Resource;
import org.concordion.integration.junit3.ConcordionTestCase;

public class SyntaxForRelativePathsTest extends ConcordionTestCase{

    public String getPath(String resourcePath, String relativePath) {
        return new Resource(resourcePath).getRelativeResource(relativePath).getPath();
    }
}
