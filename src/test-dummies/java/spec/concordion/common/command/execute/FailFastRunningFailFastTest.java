package spec.concordion.common.command.execute;

import org.concordion.api.FailFast;
import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import spec.concordion.common.results.runTotals.RunTotalsExtension;

@RunWith(ConcordionRunner.class) @FailFast
public class FailFastRunningFailFastTest {

	@Extension
	public RunTotalsExtension runTotalsExtension = new RunTotalsExtension();


	public String getSuccessString() throws Exception {
		return "Success";
	}

}
