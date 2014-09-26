package spec.concordion.results.runTotals;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.command.RunCommand;
import org.concordion.internal.command.SequentialRunStrategy;

public class RunTotals extends ConcordionTestCase {

	public Map<String, String> simulateRun(final String href) {
		final Element element = new Element(href);
		element.addAttribute("href", href);

		final String path = "/" + getClass().getName().replace('.', '/');

		final Resource resource = new Resource(path);

		final RunCommand command = new RunCommand(new SequentialRunStrategy());
		final CommandCall commandCall = new CommandCall(command, element, "concordion", resource);

		final SummarizingResultRecorder recorder = new SummarizingResultRecorder();
		recorder.setSpecificationDescription("");

		command.execute(commandCall, null, recorder);

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
