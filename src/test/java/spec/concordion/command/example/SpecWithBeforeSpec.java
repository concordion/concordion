package spec.concordion.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.BeforeSpecification;
import org.concordion.api.ConcordionScoped;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.api.Scope;

public class SpecWithBeforeSpec {

    @ConcordionScoped(Scope.SPECIFICATION)
    protected ScopedObjectHolder<AtomicInteger> beforeSpecCounter = new ScopedObjectHolder<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        };
    };

    @BeforeSpecification
    public void beforeSpec() {
        beforeSpecCounter.get().getAndIncrement();
    }

    public boolean beforeSpecRunOnce() {
        return beforeSpecCounter.get().get() == 1;
    }
}
