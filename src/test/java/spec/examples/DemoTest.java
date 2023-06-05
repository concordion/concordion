package spec.examples;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.ConcordionResources;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@ConcordionResources(value="/concordion.css", includeDefaultStyling = false)
public class DemoTest {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
