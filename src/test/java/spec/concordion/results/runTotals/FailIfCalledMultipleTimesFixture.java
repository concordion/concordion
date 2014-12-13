package spec.concordion.results.runTotals;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TimW5 on 26/09/14.
 *
 * @author TimW5
 */
@RunWith(ConcordionRunner.class)
public class FailIfCalledMultipleTimesFixture {
    public static AtomicInteger numberTimesRun = new AtomicInteger(0);

    public int incrementAndGet() {
        return numberTimesRun.incrementAndGet();
    }

}
