package spec.concordion.command.execute;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.FullOGNL;
import org.concordion.api.Resource;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.command.RunCommand;
import org.junit.runner.RunWith;
import spec.concordion.results.runTotals.RunTotalsFixture;

import java.util.HashMap;
import java.util.Map;

@RunWith(ConcordionRunner.class)
@FullOGNL
public class FailFastPassthroughFixture extends RunTotalsFixture {

}
