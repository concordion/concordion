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
public class NonStringTest {
    
    public String outcomeOfPerformingAssertEquals(String fragment, String expectedString, String result, String resultType) {
        
        Object simulatedResult;
        if (resultType.equals("String")) {
            simulatedResult = result;
        } else if (resultType.equals("Integer")) {
            simulatedResult = Integer.valueOf(result);
        } else if (resultType.equals("Double")) {
            simulatedResult = Double.valueOf(result);
        } else {
            throw new RuntimeException("Unsupported result-type '" + resultType + "'.");
        }
        
        fragment = fragment.replaceAll("\\(some expectation\\)", expectedString);
        
        return new TestRig()
            .withStubbedEvaluationResult(simulatedResult)
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
