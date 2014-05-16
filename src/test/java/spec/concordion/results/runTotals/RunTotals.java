package spec.concordion.results.runTotals;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.command.RunCommand;

public class RunTotals extends ConcordionTestCase {

	public ResultSummary simulateRun(final String href) {
		final Element element = new Element(href);
		element.addAttribute("href", href);
//		element.addAttribute("concordion:run", "concordion");

		final String path = "/" + getClass().getName().replace('.', '/');

		final Resource resource = new Resource(path);

		final RunCommand command = new RunCommand();
		final CommandCall commandCall = new CommandCall(command, element, "concordion", resource);

		final SummarizingResultRecorder recorder = new SummarizingResultRecorder();

		command.execute(commandCall, null, recorder);

		return recorder;
	}

}
