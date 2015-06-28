package spec.concordion.results.runTotals.testsuite.exceptionInBeforeMethod;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class ExceptionInBeforeMethod {
    @Before 
    public void setUp() {
        throw new NullPointerException("dummy exception");
    }
}
