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
public class NestedElementsTest {
    
    public String matchOrNotMatch(String snippet, String evaluationResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(snippet)
            .hasFailures() ? "not match" : "match";
    }

}
