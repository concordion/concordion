package spec.concordion.extension.listener;

import java.util.Arrays;
import java.util.List;

import spec.concordion.extension.AbstractExtensionTestCase;
import test.concordion.extension.LoggingExtension;

public class VerifyRowsListenerTest extends AbstractExtensionTestCase {

    public void addLoggingExtension() {
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }
    
    public List<String> getGeorgeAndRingo() {
        return Arrays.asList(new String[] {"George Harrison", "Ringo Starr"});
    }
}
