package spec.concordion.common.extension.listener;

import java.util.Arrays;
import java.util.List;

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
public class VerifyRowsListenerTest extends AbstractExtensionTestCase {

    public void addLoggingExtension() {
        setExtension(new LoggingExtension().withStream(getLogStream()));
    }
    
    public List<String> getGeorgeAndRingo() {
        return Arrays.asList(new String[] {"George Harrison", "Ringo Starr"});
    }
}
