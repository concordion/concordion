package spec.concordion;


import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class ExampleTest {

    public String process(String html) {
        return new TestRig()
            .withFixture(this)
            .process(html)
            .successOrFailureInWords()
            .toLowerCase();
    }
    
    public String getGreeting() {
        return "Hello World!";
    }
}
