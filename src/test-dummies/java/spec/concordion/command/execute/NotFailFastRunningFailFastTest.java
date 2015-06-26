package spec.concordion.command.execute;

import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import spec.concordion.results.runTotals.RunTotalsExtension;

@RunWith(ConcordionRunner.class)
public class NotFailFastRunningFailFastTest {

	@Extension
	public RunTotalsExtension runTotalsExtension = new RunTotalsExtension();


	public String getSuccessString() throws Exception {
		return "Success";
	}

}
