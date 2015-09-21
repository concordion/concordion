package spec.concordion.extension.listener;

import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.extension.AbstractExtensionTestCase;
import test.concordion.extension.ExampleTestExtension;

@RunWith(ConcordionRunner.class)
public class ExampleListenerTest extends AbstractExtensionTestCase {
	@Extension
	public ExampleTestExtension extension = new ExampleTestExtension().withStream(getLogStream());
	
//    public void addExampleExtension() {
//        setExtension(new ExampleTestExtension().withStream(getLogStream()));
//    }
        
    public double sqrt(double num) {
        return Math.sqrt(num);
    }
}
