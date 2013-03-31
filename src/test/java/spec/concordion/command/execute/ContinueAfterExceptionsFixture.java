package spec.concordion.command.execute;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ContinueAfterExceptionsFixture {

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

