package spec.concordion.extension.listener;

import spec.concordion.extension.AbstractExtensionTestCase;
import test.concordion.extension.LoggingExtension;

public class ListenerTest extends AbstractExtensionTestCase {

    public void addLoggingExtension() {
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }
    
    public double sqrt(double num) {
        return Math.sqrt(num);
    }
    
    public boolean isPositive(int num) {
        return num > 0;
    }
}
