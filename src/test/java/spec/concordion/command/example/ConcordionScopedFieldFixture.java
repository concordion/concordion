package spec.concordion.command.example;

import org.concordion.api.ExampleScoped;
import org.concordion.api.GloballyScoped;
import org.concordion.api.SpecificationScoped;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tim on 2/12/15.
 */
@RunWith(ConcordionRunner.class)
public class ConcordionScopedFieldFixture {
    @ExampleScoped
    private AtomicInteger exampleScopedCounter;

    @SpecificationScoped
    private AtomicInteger specScopedCounter;

    @GloballyScoped
    private AtomicInteger globalScopedCounter;

    private int fieldCounter = 0;

    public int getFieldCounter() {
        return fieldCounter;
    }

    public int getSpecScopedCounter() {
        return specScopedCounter.get();
    }

    public int getExampleScopedCounter() {
        return exampleScopedCounter.get();
    }

    public int getGlobalScopedCounter() {
        return globalScopedCounter.get();
    }

    public void incrementAllCounters() {
        fieldCounter++;
        specScopedCounter.addAndGet(1);
        exampleScopedCounter.addAndGet(1);
        globalScopedCounter.addAndGet(1);
    }


}
