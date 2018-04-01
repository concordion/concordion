package test.concordion;

import org.concordion.api.*;
import org.concordion.internal.*;
import org.concordion.internal.command.RunCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RunCommandSimulator {
    public Map<String, String> simulate(String href, Class<?> testClass, Fixture fixture) {
        final Element element = new Element("a");
        element.addAttribute("href", href);

        final String path = "/" + testClass.getName().replace('.', '/');

        final Resource resource = new Resource(path);
        File parentFile = new FileTarget(ConcordionBuilder.getBaseOutputDir()).getFile(resource).getParentFile();

        final RunCommand command = new RunCommand();
        command.setRunnerFactory(new SystemPropertiesRunnerFactory());

        final CommandCall commandCall = new CommandCall(null, command, element, "concordion", resource);

        final SummarizingResultRecorder recorder = new SummarizingResultRecorder();
        recorder.setSpecificationDescription("");

        try {
            commandCall.execute(null, recorder, fixture);
        } catch (FailFastException ffe) {
            System.out.println("Caught fail fast exception thrown by the fixture under test. Ignoring...");
        }

        File fileName = new File(parentFile, href);
//		System.out.println(fileName.getAbsolutePath());
        boolean isOutputGenerated = fileName.exists();

        Map<String, String> result = createMap(recorder, isOutputGenerated);
        return result;
    }

    public Map<String, String> createMap(ResultSummary recorder, boolean isOutputGenerated) {
        Map<String, String> result = new HashMap<String, String>();

        result.put("isOutputGenerated", isOutputGenerated ? "Yes" : "No");
        result.put("successCount", Long.toString(recorder.getSuccessCount()));
        result.put("failureCount", Long.toString(recorder.getFailureCount()));
        result.put("ignoredCount", Long.toString(recorder.getIgnoredCount()));
        result.put("exceptionCount", Long.toString(recorder.getExceptionCount()));

        String counts = recorder.printCountsToString(new FixtureType(Object.class));

        result.put("totalsString", counts);
        return result;
    }
}
