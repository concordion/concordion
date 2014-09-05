package spec.concordion.command;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class CaseInsensitiveCommandsTest {
	
	public String process(String snippet, Object stubbedResult) throws Exception {
		long successCount = new TestRig()
			.withStubbedEvaluationResult(stubbedResult)
			.processFragment(snippet)
			.getSuccessCount();
		
		return successCount == 1 ? snippet : "Did not work";
	}
}
