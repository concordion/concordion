package spec.concordion.extension.listener;

import spec.concordion.extension.AbstractExtensionTestCase;
import test.concordion.extension.LoggingExtension;

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
