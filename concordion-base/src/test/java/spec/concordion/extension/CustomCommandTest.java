package spec.concordion.extension;

import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.extension.CommandExtension;

@RunWith(ConcordionRunner.class)
public class CustomCommandTest extends AbstractExtensionTestCase {

    public void addCommandExtension() {
        setExtension(new CommandExtension().withStream(getLogStream()));
    }
    
    public List<String> getOutput() {
        return getEventLog();
    }
}
