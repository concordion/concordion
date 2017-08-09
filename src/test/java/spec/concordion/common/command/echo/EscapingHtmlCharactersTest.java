package spec.concordion.common.command.echo;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;


@RunWith(ConcordionRunner.class)
public class EscapingHtmlCharactersTest {

    public String render(String fragment, String evalResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evalResult)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    