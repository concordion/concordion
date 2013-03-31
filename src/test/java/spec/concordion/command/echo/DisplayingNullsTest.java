package spec.concordion.command.echo;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;


public class DisplayingNullsTest extends ConcordionTestCase {

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(null)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    