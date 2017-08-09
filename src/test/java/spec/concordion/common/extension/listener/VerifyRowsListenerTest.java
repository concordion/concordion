package spec.concordion.common.extension.listener;

import java.util.Arrays;
import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.common.extension.AbstractExtensionTestCase;
import test.concordion.extension.LoggingExtension;

@RunWith(ConcordionRunner.class)
public class VerifyRowsListenerTest extends AbstractExtensionTestCase {

    public void addLoggingExtension() {
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }
    
    public List<String> getGeorgeAndRingo() {
        return Arrays.asList(new String[] {"George Harrison", "Ringo Starr"});
    }
}
