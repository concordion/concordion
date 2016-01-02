package spec.concordion.results.runTotals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FileTarget;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.command.RunCommand;
import org.concordion.internal.util.IOUtil;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class RunTotalsFixture {

	public Map<String, String> simulateRun(final String href) {
		final Element element = new Element("a");
		element.addAttribute("href", href);

		final String path = "/" + getClass().getName().replace('.', '/');

		final Resource resource = new Resource(path);
		File parentFile = new FileTarget(ConcordionBuilder.getBaseOutputDir(), new IOUtil()).getFile(resource).getParentFile();

		final RunCommand command = new RunCommand();

		final CommandCall commandCall = new CommandCall(command, element, "concordion", resource);

		final SummarizingResultRecorder recorder = new SummarizingResultRecorder();
		recorder.setSpecificationDescription("");

		try {
			commandCall.execute(null, recorder);
		} catch (FailFastException ffe) {
			System.out.println("Caught fail fast exception thrown by the fixture under test. Ignoring...");
		}

		File fileName = new File(parentFile, href);
		System.out.println(fileName.getAbsolutePath());
        boolean isOutputGenerated = fileName.exists();

		Map<String, String> result = new HashMap<String, String>();

        result.put("isOutputGenerated", isOutputGenerated ? "Yes" : "No");
		result.put("successCount", Long.toString(recorder.getSuccessCount()));
		result.put("failureCount", Long.toString(recorder.getFailureCount()));
		result.put("ignoredCount", Long.toString(recorder.getIgnoredCount()));
		result.put("exceptionCount", Long.toString(recorder.getExceptionCount()));

		String counts = recorder.printCountsToString(recorder);

		result.put("totalsString", counts);
		return result;
	}

}
