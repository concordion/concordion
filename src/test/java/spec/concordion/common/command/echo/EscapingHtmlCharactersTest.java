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
public class EscapingHtmlCharactersTest {

    public String render(String fragment, String evalResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evalResult)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    