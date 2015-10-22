package spec.concordion.annotation;

import org.concordion.api.Resources;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@Resources(value = "/concordion.css", includeDefaultStyling = false)
public class DemoParent {
    
}
