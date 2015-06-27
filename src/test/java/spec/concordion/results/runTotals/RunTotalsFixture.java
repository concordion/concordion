package spec.concordion.results.runTotals;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.FailFastException;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.command.RunCommand;
import org.concordion.internal.command.SpecificationCommand;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class RunTotalsFixture {

	public Map<String, String> simulateRun(final String href) {
		final Element element = new Element(href);
		element.addAttribute("href", href);

		final String path = "/" + getClass().getName().replace('.', '/');

		final Resource resource = new Resource(path);

		final RunCommand command = new RunCommand();

		final CommandCall commandCall = new CommandCall(command, element, "concordion", resource);

		final SummarizingResultRecorder recorder = new SummarizingResultRecorder();
		recorder.setSpecificationDescription("");

		try {
			commandCall.execute(null, recorder);
		} catch (FailFastException ffe) {
			System.out.println("Caught fail fast exception thrown by the fixture under test. Ignoring...");
		}


		Map<String, String> result = new HashMap<String, String>();

		result.put("successCount", Long.toString(recorder.getSuccessCount()));
		result.put("failureCount", Long.toString(recorder.getFailureCount()));
		result.put("ignoredCount", Long.toString(recorder.getIgnoredCount()));
		result.put("exceptionCount", Long.toString(recorder.getExceptionCount()));

		String counts = recorder.printCountsToString(recorder);

		result.put("totalsString", counts);
		return result;
	}

}
