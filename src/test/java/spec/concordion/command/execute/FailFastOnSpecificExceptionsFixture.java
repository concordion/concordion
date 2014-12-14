package spec.concordion.command.execute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.concordion.api.FailFast;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
@FailFast(onExceptionType={RuntimeException.class, IOException.class})
public class FailFastOnSpecificExceptionsFixture {

    private boolean myMethodWasCalled;
    
    public boolean myMethodWasCalledProcessing(String fragment) {
        myMethodWasCalled = false;
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        return myMethodWasCalled;
    }
    
    public void myTimeoutExceptionThrower() throws TimeoutException {
        throw new TimeoutException("Thrown by myTimeoutExceptionThrower");
    }
    
    public void myFileNotFoundExceptionThrower() throws FileNotFoundException {
        throw new FileNotFoundException("Thrown by myFileNotFoundExceptionThrower");
    }
    
    public void myMethod() {
        myMethodWasCalled = true;
    }
}

