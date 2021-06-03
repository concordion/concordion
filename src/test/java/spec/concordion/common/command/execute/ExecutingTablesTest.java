package spec.concordion.common.command.execute;

import org.concordion.api.FullOGNL;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(ConcordionRunner.class)
@FullOGNL
public class ExecutingTablesTest {
    private List<Map<String, String>> rows = new ArrayList<>();

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

    public String processRow(Map<String, String> row) {
        System.out.println(row);
        rows.add(row);
        return generateUsername(row.get("First Name") + " " + row.get("Last Name"));
    }

    public List<Map<String, String>> getLoggedRows() {
        return rows;
    }

    public void resetRowMap() {
        rows = new ArrayList<>();
    }

    class Result {
        public long successCount;
        public long failureCount;
        public long exceptionCount;
        public String lastExpectedValue;
        public Object lastActualValue;
    }
    
}
