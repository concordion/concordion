package spec.concordion.command.assertEquals;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ExceptionsTest {
    
    public Object countsFromExecutingSnippetWithSimulatedEvaluationResult(String snippet, String simulatedResult) {
        TestRig harness = new TestRig();
        if (simulatedResult.equals("(An exception)")) {
            harness.withStubbedEvaluationResult(new RuntimeException("simulated exception"));
        } else {
            harness.withStubbedEvaluationResult(simulatedResult);
        }
        return harness.processFragment(snippet);
    }
}
