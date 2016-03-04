package spec.concordion.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FixtureWithExampleInitialisationFixture {

    private static AtomicInteger staticCounter = new AtomicInteger();
    
    private int counter = incrementOnEachCall();

    private int incrementOnEachCall() {
        return staticCounter.incrementAndGet();
    }

    public int getCounter() {
        return counter;
    }
}
