package spec.concordion.common.extension;

import java.util.List;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.extension.CommandExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class CustomCommandTest extends AbstractExtensionTestCase {

    public void addCommandExtension() {
        setExtension(new CommandExtension().withStream(getLogStream()));
    }
    
    public List<String> getOutput() {
        return getEventLog();
    }
}
