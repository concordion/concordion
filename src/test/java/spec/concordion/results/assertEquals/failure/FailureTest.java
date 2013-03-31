package spec.concordion.results.assertEquals.failure;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class FailureTest extends ConcordionTestCase {
    
    public String acronym;

    public String renderAsFailure(String fragment, String acronym) throws Exception {
        this.acronym = acronym;
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
