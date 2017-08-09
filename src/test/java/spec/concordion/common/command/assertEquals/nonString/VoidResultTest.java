package spec.concordion.common.command.assertEquals.nonString;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
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
