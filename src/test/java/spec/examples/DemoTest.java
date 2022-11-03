package spec.examples;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.ConcordionResources;

// @RunWith(ConcordionRunner.class)
@ConcordionFixture
@ConcordionResources(value="/concordion.css", includeDefaultStyling = false)
public class DemoTest {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
