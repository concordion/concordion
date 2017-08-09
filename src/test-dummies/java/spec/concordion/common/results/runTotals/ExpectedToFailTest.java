package spec.concordion.common.results.runTotals;

import org.concordion.api.ExpectedToFail;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@ExpectedToFail
public class ExpectedToFailTest {

	public String getSuccessString() {
		return "Success";
	}

	public String throwException() throws Exception {
		throw new Exception();
	}

}
