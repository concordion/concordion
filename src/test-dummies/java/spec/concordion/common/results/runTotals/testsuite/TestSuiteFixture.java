package spec.concordion.common.results.runTotals.testsuite;

import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import spec.concordion.common.results.runTotals.RunTotalsExtension;

@RunWith(ConcordionRunner.class)
public class TestSuiteFixture {
    @Extension
    public RunTotalsExtension runTotalsExtension = new RunTotalsExtension();
}
