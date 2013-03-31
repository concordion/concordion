package spec.concordion.command.execute;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class ExecuteTest extends ConcordionTestCase {

    private boolean myMethodWasCalled = false;
    
    public boolean myMethodWasCalledProcessing(String fragment) {
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        return myMethodWasCalled;
    }
    
    public void myMethod() {
        myMethodWasCalled = true;
    }
}
