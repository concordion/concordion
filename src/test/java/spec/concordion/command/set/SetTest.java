package spec.concordion.command.set;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class SetTest extends ConcordionTestCase {

    private String param;

    public void process(String fragment) {
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
    }
    
    public String getParameterPassedIn() {
        return param;
    }
    
    public void setUpUser(String fullName) {
        this.param = fullName;
    }
}
