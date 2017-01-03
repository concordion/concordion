package spec.concordion.integration.junit4;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FooFixture {
    public FooFixture() {
        FooFixtureRecorder.setFooFixtureClass(FooFixture.class.getSimpleName());
    }
}
