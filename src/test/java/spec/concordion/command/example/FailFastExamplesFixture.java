package spec.concordion.command.example;

import org.concordion.api.FailFast;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

/**
 * Created by tim on 2/07/15.
 */
@RunWith(ConcordionRunner.class)
@FullOGNL
@FailFast
public class FailFastExamplesFixture {

    private int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void throwException() throws Exception{
        throw new Exception();
    }
}
