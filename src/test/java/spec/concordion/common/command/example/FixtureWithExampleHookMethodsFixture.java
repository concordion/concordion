package spec.concordion.common.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.AfterExample;
import org.concordion.api.BeforeExample;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FixtureWithExampleHookMethodsFixture {

    private static AtomicInteger staticBeforeExampleCounter = new AtomicInteger();
    private static AtomicInteger staticAfterExampleCounter = new AtomicInteger();
    
    @BeforeExample
    private void beforeEachExample() {
        staticBeforeExampleCounter.incrementAndGet();
    }
    
    @AfterExample
    private void afterEachExample() {
        staticAfterExampleCounter.incrementAndGet();
    }
    
    public int getBeforeExampleCounter() {
        return staticBeforeExampleCounter.get();
    }

    public int getAfterExampleCounter() {
        return staticAfterExampleCounter.get();
    }
}
