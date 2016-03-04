package spec.concordion.command.example;

import java.util.Map;

import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.results.runTotals.RunTotalsFixture;

@RunWith(ConcordionRunner.class)
@FullOGNL
public class ExampleFixture extends SpecWithBeforeSpec {

    private int counter = 0;

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
}
