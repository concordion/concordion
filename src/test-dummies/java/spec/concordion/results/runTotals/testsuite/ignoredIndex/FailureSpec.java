package spec.concordion.results.runTotals.testsuite.ignoredIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FailureSpec {
    public void sleep(int secs) throws InterruptedException {
        Thread.sleep(secs);
    }
}
