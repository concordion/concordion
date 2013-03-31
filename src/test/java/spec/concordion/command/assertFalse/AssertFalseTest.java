package spec.concordion.command.assertFalse;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class AssertFalseTest extends ConcordionTestCase {
    
    public String successOrFailure(String fragment, String evaluationResult) {
        return new TestRig()
            .withStubbedEvaluationResult(new Boolean(evaluationResult))
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
