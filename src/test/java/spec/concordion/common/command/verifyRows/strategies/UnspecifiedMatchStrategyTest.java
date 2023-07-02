package spec.concordion.common.command.verifyRows.strategies;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class UnspecifiedMatchStrategyTest extends BaseMatchStrategyTest {
}
