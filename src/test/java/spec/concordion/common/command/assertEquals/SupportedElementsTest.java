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
public class SupportedElementsTest {
    
    public String process(String snippet) throws Exception {
        long successCount = new TestRig()
            .withStubbedEvaluationResult("Fred")
            .processFragment(snippet)
            .getSuccessCount();
        
        return successCount == 1 ? snippet : "Did not work";
    }

}
