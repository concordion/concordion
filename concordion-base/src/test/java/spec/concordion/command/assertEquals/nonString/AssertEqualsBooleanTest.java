package spec.concordion.command.assertEquals.nonString;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class AssertEqualsBooleanTest {
    
    public String successOrFailure(String fragment, String value, String evaluationResult) {
        String specFragment = fragment.replace("value", value);
        boolean stubbedResult = Boolean.valueOf(evaluationResult).booleanValue();
        return new TestRig()
            .withStubbedEvaluationResult(stubbedResult)
            .processFragment(specFragment)
            .successOrFailureInWords();
    }
}
