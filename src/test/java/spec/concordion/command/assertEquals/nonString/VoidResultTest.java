package spec.concordion.command.assertEquals.nonString;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

public class VoidResultTest extends ConcordionTestCase {

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
