package spec.concordion.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FixtureWithoutExampleInitialisationFixture {

    private static AtomicInteger staticFieldCounter = new AtomicInteger();
    
    private int counter;

    public FixtureWithoutExampleInitialisationFixture() {
        counter = staticFieldCounter.incrementAndGet();
    }

    public int getFieldInitialisationCounter() {
        return counter;
    }
}
