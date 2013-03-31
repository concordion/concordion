package spec.concordion.command.assertEquals;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class AssertEqualsTest extends ConcordionTestCase {
    
    public String successOrFailure(String fragment, String evaluationResult) {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
