package spec.concordion.common.command.run;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import spec.concordion.common.results.runTotals.RunTotalsFixture;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class HrefUpdates extends RunTotalsFixture {
}
