package spec.concordion.command.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.BeforeSpecification;
import org.concordion.api.SpecificationScoped;

/**
 * Created by tim on 10/12/15.
 */
public class SpecWithBeforeSpec {

    protected SpecificationScoped<AtomicInteger> beforeSpecCounter = new SpecificationScoped<AtomicInteger>() {
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
