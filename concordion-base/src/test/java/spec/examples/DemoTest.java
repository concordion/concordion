package spec.examples;

import org.concordion.api.ConcordionResources;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@ConcordionResources(value="/concordion.css", includeDefaultStyling = false)
public class DemoTest {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
