package spec.concordion.common.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
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
