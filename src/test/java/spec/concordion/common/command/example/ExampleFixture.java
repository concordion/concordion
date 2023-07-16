package spec.concordion.common.command.example;

import java.util.Map;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.common.results.runTotals.RunTotalsFixture;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@FullOGNL
public class ExampleFixture extends SpecWithBeforeSpec {

    private int counter = 0;
    private int numberRowsInserted = 0;

    public void setCounter(String val) {
        counter = Integer.parseInt(val);
    }

    public int getCounter() {
        return counter;
    }

    public synchronized int incrementCounter() {
        counter++;
        return counter;
    }

    public boolean isTrue() {
        return true;
    }

    public boolean isNull(String object) {
        return object == null;
    }

    public Map<String, Object> runTestDummySpec(String href) throws Exception {
        return new RunTotalsFixture().withTestClass(this.getClass()).simulateRun(href);
    }

    public int numberTableRowsInserted() {
        return numberRowsInserted;
    }

    public void addTableRow(String description) {
        numberRowsInserted++;
    }
}
