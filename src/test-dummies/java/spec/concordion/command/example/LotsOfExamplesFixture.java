package spec.concordion.command.example;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class LotsOfExamplesFixture {

    public boolean isTrue() {
        return true;
    }

    public boolean throwException() throws Exception {
        throw new Exception();
    }

}
