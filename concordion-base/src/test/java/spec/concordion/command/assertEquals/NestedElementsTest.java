package spec.concordion.command.assertEquals;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class NestedElementsTest {
    
    public String matchOrNotMatch(String snippet, String evaluationResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(snippet)
            .hasFailures() ? "not match" : "match";
    }

}
