package spec.concordion.command.example;

import org.concordion.api.BeforeSpecification;
import org.concordion.api.FullOGNL;
import org.concordion.api.SpecificationScoped;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import spec.concordion.results.runTotals.RunTotalsFixture;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tim on 2/07/15.
 */
@RunWith(ConcordionRunner.class)
@FullOGNL
public class ExamplesFixture {


    private int counter = 0;

    @SpecificationScoped
    AtomicInteger beforeSpecCounter;

    @BeforeSpecification
    public void beforeSpec() {
        beforeSpecCounter.getAndIncrement();
    }

    public void setCounter(String val) {
        counter = Integer.parseInt(val);
    }

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter++;
    }

    public boolean isTrue() {
        return true;
    }

    public boolean isNull(String object) {
        return object == null;
    }

    public Map<String, String> runTestDummySpec(String href) throws Exception {
        return new RunTotalsFixture().withTestClass(this.getClass()).simulateRun(href);
    }

    public boolean beforeSpecRunOnce() {
        return beforeSpecCounter.get() == 1;
    }
}
