package spec.concordion;


import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
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
