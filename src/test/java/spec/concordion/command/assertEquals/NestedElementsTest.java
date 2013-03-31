package spec.concordion.command.assertEquals;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class NestedElementsTest extends ConcordionTestCase {
    
    public String matchOrNotMatch(String snippet, String evaluationResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(snippet)
            .hasFailures() ? "not match" : "match";
    }

}
