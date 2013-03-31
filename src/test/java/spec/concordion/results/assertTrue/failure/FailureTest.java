package spec.concordion.results.assertTrue.failure;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class FailureTest extends ConcordionTestCase {
    
    public boolean isPalindrome(String s) {
        return new StringBuilder(s).reverse().toString().equals(s); 
    }

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
