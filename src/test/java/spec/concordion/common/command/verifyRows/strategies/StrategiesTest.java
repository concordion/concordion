package spec.concordion.common.command.verifyRows.strategies;

import static org.concordion.api.MultiValueResult.multiValueResult;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.MultiValueResult;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class StrategiesTest extends BaseMatchStrategyTest {
    
    public MultiValueResult processTableFragment(String fragment, String actualData) throws Exception {
        String resultTable = processFragment(fragment, actualData);

        return multiValueResult()
                .with("expectedTableCommented", "<!--" + fragment + "-->")
                .with("resultTableCommented", "<!--" + resultTable + "-->")
                .with("resultTable", resultTable);
        
    }    
}
