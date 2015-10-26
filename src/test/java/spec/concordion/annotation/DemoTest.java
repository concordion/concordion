package spec.concordion.annotation;

import org.concordion.api.Resources;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

//TODO Delete me and other Demo files when happy with Resources annotation
@RunWith(ConcordionRunner.class)
@Resources(value = {"demo.css", "demo.js", "demo.txt"})
public class DemoTest {
	
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
