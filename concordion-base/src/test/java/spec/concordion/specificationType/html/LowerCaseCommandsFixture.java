package spec.concordion.specificationType.html;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class LowerCaseCommandsFixture {
	
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