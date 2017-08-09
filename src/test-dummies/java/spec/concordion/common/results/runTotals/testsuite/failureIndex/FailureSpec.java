package spec.concordion.common.results.runTotals.testsuite.failureIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FailureSpec {
    public void sleep(int secs) throws InterruptedException {
        Thread.sleep(secs);
    }
}
