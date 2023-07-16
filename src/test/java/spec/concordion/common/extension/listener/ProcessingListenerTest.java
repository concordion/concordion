package spec.concordion.common.extension.listener;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.common.extension.AbstractExtensionTestCase;
import test.concordion.extension.ExampleProcessingExtension;
import test.concordion.extension.ProcessingExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class ProcessingListenerTest extends AbstractExtensionTestCase {
    public void addProcessingExtension() {
        setExtension(new ProcessingExtension().withStream(getLogStream()));
    }
    
    public void addExampleProcessingExtension() {
        setExtension(new ExampleProcessingExtension().withStream(getLogStream()));
    }
    
    public double sqrt(double num) {
        return Math.sqrt(num);
    }
}
