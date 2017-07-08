package spec.concordion.results.runTotals;

import org.concordion.api.Unimplemented;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class) @Unimplemented 
public class UnimplementedWithAssertTest {

	public String getSuccessString() {
		return "Success";
	}

	public String throwException() throws Exception {
		throw new Exception();
	}

	public boolean doTest() {
		return true;
	}

}
