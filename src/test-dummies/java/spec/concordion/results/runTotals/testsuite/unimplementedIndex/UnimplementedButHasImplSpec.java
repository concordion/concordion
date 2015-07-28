package spec.concordion.results.runTotals.testsuite.unimplementedIndex;

import org.concordion.api.Unimplemented;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@Unimplemented
public class UnimplementedButHasImplSpec {
    public void sleep(int secs) throws InterruptedException {
        Thread.sleep(secs);
    }
}
