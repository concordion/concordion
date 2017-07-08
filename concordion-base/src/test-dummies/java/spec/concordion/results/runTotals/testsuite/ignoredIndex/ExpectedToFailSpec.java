package spec.concordion.results.runTotals.testsuite.ignoredIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@org.concordion.api.ExpectedToFail
public class ExpectedToFailSpec {
    public void sleep(int secs) throws InterruptedException {
        Thread.sleep(secs);
    }
}
