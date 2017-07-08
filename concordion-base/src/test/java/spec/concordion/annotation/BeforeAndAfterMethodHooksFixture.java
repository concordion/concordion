package spec.concordion.annotation;

import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.RunCommandSimulator;

@RunWith(ConcordionRunner.class)
public class BeforeAndAfterMethodHooksFixture {
    public List<String> getLog() {
        return BeforeAndAfterLoggingFixture.getLog();
    }

    public List<String> getListenerLog() {
        return BeforeAndAfterLoggingWithListenerFixture.getLog();
    }

    public void simulateRun(final String href) throws Exception {
        new RunCommandSimulator().simulate(href, this.getClass());
    }
}
