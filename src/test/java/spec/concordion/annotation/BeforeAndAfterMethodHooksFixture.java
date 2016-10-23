package spec.concordion.annotation;

import java.io.File;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.*;
import org.concordion.internal.command.RunCommand;
import org.junit.runner.RunWith;
import test.concordion.RunCommandSimulator;

@RunWith(ConcordionRunner.class)
public class BeforeAndAfterMethodHooksFixture {
    public List<String> getLog() {
        return BeforeAndAfterLoggingFixture.getLog();
    }

    public void simulateRun(final String href) throws Exception {
        new RunCommandSimulator().simulate(href, this.getClass());
    }
}
