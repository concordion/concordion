package spec.concordion.common.extension.listener;

import java.util.List;

import org.concordion.api.BeforeSpecification;
import org.concordion.api.ConcordionFixture;
import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import spec.concordion.common.extension.AbstractExtensionTestCase;
import test.concordion.extension.ExampleTestExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class ExampleListenerTest extends AbstractExtensionTestCase {

	@Extension
	public ExampleTestExtension extension = new ExampleTestExtension();

    @BeforeSpecification
    public void beforeSpec() {
        extension.withStream(getLogStream());
    }

    public double sqrt(double num) {
        return Math.sqrt(num);
    }

    public String generateUsername(String fullName) {
        return fullName.replaceAll(" ", "").toLowerCase();
    }

    public List<String> getEventLogExcludingCheck() {
        List<String> eventLog = getEventLog();
        eventLog.remove("Before example 'check-basic-example'");
        eventLog.remove("After example 'check-basic-example' - passed: 2, failed: 0, exceptions: 0");
        eventLog.remove("Before example 'check-table-example'");
        return eventLog;
    }
}
