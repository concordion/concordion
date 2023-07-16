package spec.concordion.common.command.example;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.FailFast;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@FullOGNL
@FailFast
public class FailFastExampleFixture {

    private int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void throwException() throws Exception{
        throw new Exception();
    }
}
