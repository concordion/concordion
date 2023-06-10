package spec.concordion.common.results.assertEquals.failure;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class FailureTest {
    
    public String acronym;

    public String renderAsFailure(String fragment, String acronym) throws Exception {
        this.acronym = acronym;
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
