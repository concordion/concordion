package spec.concordion.annotation;

import org.concordion.api.CopyResource;
import org.concordion.api.CopyResource.InsertType;

//@RunWith(ConcordionRunner.class)
@CopyResource(sourceFiles = { "*.css", "demo.txt" }, insertType = InsertType.LINKED)
public class DemoTest extends DemoParent {
    
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
