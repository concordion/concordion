package spec.concordion.extension.listener;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.extension.AbstractExtensionTestCase;
import test.concordion.extension.ExampleProcessingExtension;
import test.concordion.extension.LoggingExtension;
import test.concordion.extension.ProcessingExtension;

@RunWith(ConcordionRunner.class)
public class ListenerTest extends AbstractExtensionTestCase {

    public void addLoggingExtension() {
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }
    
    public void addProcessingExtension() {
        setExtension(new ProcessingExtension().withStream(getLogStream()));
    }
    
    public void addExampleProcessingExtension() {
        setExtension(new ExampleProcessingExtension().withStream(getLogStream()));
    }
    
    public double sqrt(double num) {
        return Math.sqrt(num);
    }
    
    public boolean isPositive(int num) {
        return num > 0;
    }
}
