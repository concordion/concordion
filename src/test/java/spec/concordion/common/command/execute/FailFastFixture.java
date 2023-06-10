package spec.concordion.common.command.execute;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.FailFast;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@FailFast
public class FailFastFixture {

    private boolean myMethodWasCalled = false;
    
    public boolean myMethodWasCalledProcessing(String fragment) {
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        return myMethodWasCalled;
    }
    
    public void myExceptionThrower() {
        throw new RuntimeException("Thrown by myExceptionThrower");
    }
    
    public void myMethod() {
        myMethodWasCalled = true;
    }
}

