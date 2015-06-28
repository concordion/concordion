package spec.concordion.results.runTotals;

import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class InvalidHTMLTest {

    @Extension
    public RunTotalsExtension runTotalsExtension = new RunTotalsExtension();

}
