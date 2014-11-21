package spec.concordion.command;

import test.concordion.TestRig;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class LowerCaseCommandsTest {
	
	public String processEquals(String snippet) throws Exception {
		return process(snippet, "Fred");
	}
	
	public String processTrue(String snippet) throws Exception {
		return process(snippet, true);
	}
	
	public String processFalse(String snippet) throws Exception {
		return process(snippet, false);
	}
	
	public String process(String snippet, Object stubbedResult) throws Exception {
		long successCount = new TestRig()
			.withStubbedEvaluationResult(stubbedResult)
			.processFragment(snippet)
			.getSuccessCount();
		
		return successCount == 1 ? snippet : "Did not work";
	}
}