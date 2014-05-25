package spec.concordion.results.runTotals;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class) @Ignore
public class MixedSuccessesAndFailures {

	public String getSuccessString() {
		return "Success";
	}

	public String throwException() throws Exception {
		throw new Exception();
	}

}
