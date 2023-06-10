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
public class NullResultTest {
    
    public String outcomeOfPerformingAssertEquals(String snippet, String expectedString, String result) {
        if (result.equals("null")) {
            result = null;
        }
        snippet = snippet.replaceAll("\\(some expectation\\)", expectedString);
        
        return new TestRig()
            .withStubbedEvaluationResult(result)
            .processFragment(snippet)
            .successOrFailureInWords();
    }
}
