package spec.concordion.extension;

import java.util.List;

import test.concordion.extension.CommandExtension;

public class CustomCommandTest extends AbstractExtensionTestCase {

    public void addCommandExtension() {
        setExtension(new CommandExtension().withStream(getLogStream()));
    }
    
    public List<String> getOutput() {
        return getEventLog();
    }
}
