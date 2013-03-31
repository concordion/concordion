package spec.concordion.command.assertEquals;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class SupportedElementsTest extends ConcordionTestCase {
    
    public String process(String snippet) throws Exception {
        long successCount = new TestRig()
            .withStubbedEvaluationResult("Fred")
            .processFragment(snippet)
            .getSuccessCount();
        
        return successCount == 1 ? snippet : "Did not work";
    }

}
