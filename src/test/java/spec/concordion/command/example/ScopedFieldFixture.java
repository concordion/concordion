package spec.concordion.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.ExampleScoped;
import org.concordion.api.SpecificationScoped;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

/**
 * Created by tim on 2/12/15.
 */
@RunWith(ConcordionRunner.class)
public class ScopedFieldFixture {
    private ExampleScoped<AtomicInteger> exampleScopedCounter = new ExampleScoped<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }
    };

    private SpecificationScoped<AtomicInteger> specScopedCounter = new SpecificationScoped<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
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
