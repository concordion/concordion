package spec.concordion.command.echo;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;


public class EchoTest extends ConcordionTestCase {

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