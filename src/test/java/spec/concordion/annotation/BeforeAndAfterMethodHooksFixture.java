package spec.concordion.annotation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.BeforeExample;
import org.concordion.api.ExampleName;
import org.concordion.api.Scope;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.RunCommandSimulator;

@RunWith(ConcordionRunner.class)
public class BeforeAndAfterMethodHooksFixture {
    private String exampleName;

    public List<String> getLog() {
        return BeforeAndAfterLoggingFixture.getLog();
    }

    public List<String> getListenerLog() {
        return BeforeAndAfterLoggingWithListenerFixture.getLog();
    }

    public void simulateRun(final String href) throws Exception {
        new RunCommandSimulator().simulate(href, this.getClass());
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
