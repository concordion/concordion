package spec.concordion.command.assertEquals;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class ExceptionsTest extends ConcordionTestCase {
    
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
