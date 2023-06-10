package spec.concordion.common.extension.listener;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.common.extension.AbstractExtensionTestCase;
import test.concordion.extension.LoggingExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class ExecuteTableListenerTest extends AbstractExtensionTestCase {

    public void addLoggingExtension() {
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }

    public double sqrt(double num) {
        return Math.sqrt(num);
    }
    
    public String generateUsername(String fullName) {
        return fullName.replaceAll(" ", "").toLowerCase();
    }
}
