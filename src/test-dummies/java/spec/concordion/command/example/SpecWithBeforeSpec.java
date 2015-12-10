package spec.concordion.command.example;

import org.concordion.api.BeforeSpecification;
import org.concordion.api.SpecificationScoped;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tim on 10/12/15.
 */
public class SpecWithBeforeSpec {

    @SpecificationScoped
    protected AtomicInteger beforeSpecCounter;

    @BeforeSpecification
    public void beforeSpec() {
        beforeSpecCounter.getAndIncrement();
    }

}
