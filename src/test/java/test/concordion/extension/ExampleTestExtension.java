package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.ResultSummary;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.internal.util.SimpleFormatter;

public class ExampleTestExtension implements ConcordionExtension, ExampleListener {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withExampleListener(this);
    }
    
    private PrintStream stream;

    public ExampleTestExtension withStream(PrintStream stream) {
        this.stream = stream;
        return this;
    }

	@Override
	public void beforeExample(ExampleEvent event) {
		stream.println(SimpleFormatter.format("Before example '%s'", event.getExampleName()));
	}

	@Override
	public void afterExample(ExampleEvent event) {
		ResultSummary summary = event.getResultSummary();
		
		stream.println(SimpleFormatter.format("After example '%s' - passed: %s, failed: %s, exceptions: %s", event.getExampleName(), summary.getSuccessCount(), summary.getFailureCount(), summary.getExceptionCount()));
	}
}
