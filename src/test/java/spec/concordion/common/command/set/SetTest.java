package spec.concordion.common.command.set;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
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
