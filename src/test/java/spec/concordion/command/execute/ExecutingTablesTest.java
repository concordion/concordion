package spec.concordion.command.execute;

import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ExecutingTablesTest {

    public Result process(String fragment) throws Exception {
        
        ProcessingResult r = new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        
        Result result = new Result();
        result.successCount = r.getSuccessCount();
        result.failureCount = r.getFailureCount();
        result.exceptionCount = r.getExceptionCount();
        
        AssertFailureEvent lastEvent = r.getLastAssertFailureEvent();
        if (lastEvent != null) {
            result.lastActualValue = lastEvent.getActual();
            result.lastExpectedValue = lastEvent.getExpected();
        }
        
        return result;
    }
    
    public String generateUsername(String fullName) {
        return fullName.replaceAll(" ", "").toLowerCase();
    }

    class Result {
        public long successCount;
        public long failureCount;
        public long exceptionCount;
        public String lastExpectedValue;
        public Object lastActualValue;
    }
    
}
