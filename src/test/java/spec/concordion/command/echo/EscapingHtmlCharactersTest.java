package spec.concordion.command.echo;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;


public class EscapingHtmlCharactersTest extends ConcordionTestCase {

    public String render(String fragment, String evalResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evalResult)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    