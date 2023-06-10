package spec.concordion.common.command.assertEquals.nonString;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class VoidResultTest {

    public String process(String snippet) {
        ProcessingResult r = new TestRig()
            .withFixture(this)
            .processFragment(snippet);
        
        if (r.getExceptionCount() != 0) {
            return "exception";
        }
        
        return r.successOrFailureInWords();
    }

    public void myVoidMethod() {
    }

}
