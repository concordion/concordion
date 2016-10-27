package spec.concordion.annotation;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.ConcordionScoped;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.api.Scope;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class ConcordionScopedFixture {

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

    @org.concordion.api.ConcordionScoped(Scope.SPECIFICATION)
    private ScopedObjectHolder<AtomicInteger> specScopedCounter = new ScopedObjectHolder<AtomicInteger>() {
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

    public int getSpecScopedCounter() {
        return specScopedCounter.get().get();
    }

    public int getExampleScopedCounter() {
        return exampleScopedCounter.get().get();
    }

    public void incrementAllCounters() {
        fieldCounter++;
        specScopedCounter.get().addAndGet(1);
        exampleScopedCounter.get().addAndGet(1);
    }
}
