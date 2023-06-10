package spec.concordion.annotation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.*;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.FixtureInstance;
import org.junit.runner.RunWith;
import test.concordion.RunCommandSimulator;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class BeforeAndAfterMethodHooksFixture {
    private String exampleName;

    public List<String> getLog() {
        return BeforeAndAfterLoggingFixture.getLog();
    }

    public List<String> getListenerLog() {
        return BeforeAndAfterLoggingWithListenerFixture.getLog();
    }

    @org.concordion.api.ConcordionScoped(Scope.EXAMPLE)
    private ScopedObjectHolder<AtomicInteger> exampleScopedCounter = new ScopedObjectHolder<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }

        @Override
        protected void destroy(AtomicInteger counter) {
            counter.set(-1);
        }

    };

    private int fieldCounter = 0;

    public int getFieldCounter() {
        return fieldCounter;
    }

    public int getExampleScopedCounter() {
        return exampleScopedCounter.get().get();
    }

    @BeforeExample
    public void incrementAllCounters(@ExampleName String name) {
        exampleName = name;
        fieldCounter++;
        exampleScopedCounter.get().addAndGet(1);
    }

    public String getExampleName() {
        return exampleName;
    }
}
