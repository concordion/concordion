package spec.concordion.command.echo;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;


@RunWith(ConcordionRunner.class)
public class DisplayingNullsTest {

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(null)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    