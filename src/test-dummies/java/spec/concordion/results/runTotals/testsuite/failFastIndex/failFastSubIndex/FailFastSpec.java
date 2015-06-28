package spec.concordion.results.runTotals.testsuite.failFastIndex.failFastSubIndex;

import org.concordion.api.FailFast;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@FailFast
public class FailFastSpec {
    public void sleep(int secs) throws InterruptedException {
        throw new UnsupportedOperationException("dummy exception");
    }
}
