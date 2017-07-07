package spec.concordion.command.set;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class SetTest {

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
