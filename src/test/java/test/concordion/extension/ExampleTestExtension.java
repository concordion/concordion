package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.ResultSummary;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;

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
		stream.println("Before Example");
	}

	@Override
	public void afterExample(ExampleEvent event) {
		ResultSummary summary = event.getResultSummary();
		
		stream.println(String.format("After Example - passed: %s, failed: %s, exceptions: %s", summary.getSuccessCount(), summary.getFailureCount(), summary.getExceptionCount()));
	}
}
