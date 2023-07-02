package spec.concordion.common.command.assertEquals.nonString;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
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
