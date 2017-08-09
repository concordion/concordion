package spec.concordion.common.results.runTotals.testsuite.exceptionIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class ExceptionSpec {
    public void sleep(int secs) throws InterruptedException {
        throw new UnsupportedOperationException("dummy exception");
    }
}
