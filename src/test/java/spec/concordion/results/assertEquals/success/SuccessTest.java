package spec.concordion.results.assertEquals.success;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class SuccessTest extends ConcordionTestCase {
    
    public String username = "fred";
    
    public String renderAsSuccess(String fragment) throws Exception {
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }

}
