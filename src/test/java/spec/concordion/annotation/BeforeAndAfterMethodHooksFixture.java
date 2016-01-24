package spec.concordion.annotation;

import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class BeforeAndAfterMethodHooksFixture {
    public List<String> getLog() {
        return BeforeAndAfterLoggingFixture.getLog();
    }
}
