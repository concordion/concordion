package spec.concordion.common.command.example;

import org.concordion.api.FailFast;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@FailFast
public class FailFastFixture {

    public void throwException() throws Exception {
        throw new Exception();
    }
}
