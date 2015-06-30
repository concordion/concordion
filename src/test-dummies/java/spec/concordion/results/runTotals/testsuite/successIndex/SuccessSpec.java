package spec.concordion.results.runTotals.testsuite.successIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class SuccessSpec {
    public void sleep(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }
}
