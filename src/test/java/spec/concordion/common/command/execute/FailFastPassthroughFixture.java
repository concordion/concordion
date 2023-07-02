package spec.concordion.common.command.execute;

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
public class FailFastPassthroughFixture extends RunTotalsFixture {

}
