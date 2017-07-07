package spec.concordion.results.assertTrue.success;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class SuccessTest {
    
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
