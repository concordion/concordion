package spec.concordion.common.results.runTotals;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FailIfCalledMultipleTimesFixture {
    public static AtomicInteger numberTimesRun = new AtomicInteger(0);

    public int incrementAndGet() {
        return numberTimesRun.incrementAndGet();
    }

}
