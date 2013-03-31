package spec.concordion;


import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class ExampleTest extends ConcordionTestCase {

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
