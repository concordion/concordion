package spec.concordion.command.example;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ConcordionScopedField;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tim on 2/12/15.
 */
@RunWith(ConcordionRunner.class)
public class ConcordionScopedFieldFixture {
    @ConcordionScopedField(scope = ConcordionScopedField.Scope.SPECIFICATION)
    private AtomicInteger specScopedCounter;

    @ConcordionScopedField(scope = ConcordionScopedField.Scope.EXAMPLE)
    private AtomicInteger exampleScopedCounter;

    @ConcordionScopedField(scope = ConcordionScopedField.Scope.GLOBAL)
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
        specScopedCounter.addAndGet(1);
        exampleScopedCounter.addAndGet(1);
        globalScopedCounter.addAndGet(1);
    }


}
