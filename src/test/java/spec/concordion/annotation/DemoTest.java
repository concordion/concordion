package spec.concordion.annotation;

import org.concordion.api.Resources;

//@RunWith(ConcordionRunner.class)
@Resources( { "*.css", "*.js", "demo.txt" } )
public class DemoTest extends DemoParent {
    
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
