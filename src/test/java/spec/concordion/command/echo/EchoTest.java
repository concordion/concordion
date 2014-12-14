package spec.concordion.command.echo;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;


@RunWith(ConcordionRunner.class)
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