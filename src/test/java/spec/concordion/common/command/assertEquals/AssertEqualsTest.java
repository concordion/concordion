package spec.concordion.common.command.assertEquals;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class AssertEqualsTest {
    
    public String successOrFailure(String fragment, String evaluationResult) {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
