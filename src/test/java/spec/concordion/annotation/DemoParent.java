package spec.concordion.annotation;

import org.concordion.api.CopyResource;
import org.concordion.api.CopyResource.InsertType;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@CopyResource(sourceFiles = "/concordion.css", removeDefaultCSS = false, insertType = InsertType.EMBEDDED)
public class DemoParent {
    
}
