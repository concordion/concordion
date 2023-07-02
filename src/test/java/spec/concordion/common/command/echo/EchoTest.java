package spec.concordion.common.command.echo;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class EchoTest {

    private String nextResult;

    public void setNextResult(String nextResult) {
        this.nextResult = nextResult;
    }
    
    public String render(String fragment) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(nextResult)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
    
}    