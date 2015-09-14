package spec.concordion.extension.listener;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.extension.AbstractExtensionTestCase;
import test.concordion.extension.LoggingExtension;

@RunWith(ConcordionRunner.class)
public class ExampleListenerTest extends AbstractExtensionTestCase {

    public void addExampleExtension() {
    	//ANDREW-TODO - need the example command listener here
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }
    
    public double sqrt(double num) {
        return Math.sqrt(num);
    }
    
    public boolean isPositive(int num) {
        return num > 0;
    }
}
