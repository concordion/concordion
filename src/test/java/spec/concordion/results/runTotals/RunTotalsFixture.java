package spec.concordion.results.runTotals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.*;
import org.concordion.internal.command.RunCommand;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class RunTotalsFixture {

	private Class<?> testClass;

	public RunTotalsFixture() {
		withTestClass(getClass());
	}

	public RunTotalsFixture withTestClass(Class<?> fixtureClass) {
		this.testClass = fixtureClass;
		return this;
	}

	public Map<String, String> simulateRun(final String href) throws Exception {

		final Element element = new Element("a");
		element.addAttribute("href", href);

		final String path = "/" + testClass.getName().replace('.', '/');

		final Resource resource = new Resource(path);
		File parentFile = new FileTarget(ConcordionBuilder.getBaseOutputDir()).getFile(resource).getParentFile();

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

		Map<String, String> result = createMap(recorder, isOutputGenerated);
		return result;
	}

	private Map<String, String> createMap(ResultSummary recorder, boolean isOutputGenerated) {
		Map<String, String> result = new HashMap<String, String>();

		result.put("isOutputGenerated", isOutputGenerated ? "Yes" : "No");
		result.put("successCount", Long.toString(recorder.getSuccessCount()));
		result.put("failureCount", Long.toString(recorder.getFailureCount()));
		result.put("ignoredCount", Long.toString(recorder.getIgnoredCount()));
		result.put("exceptionCount", Long.toString(recorder.getExceptionCount()));

		String counts = recorder.printCountsToString(new Fixture(new Object()));

		result.put("totalsString", counts);
		return result;
	}
}
