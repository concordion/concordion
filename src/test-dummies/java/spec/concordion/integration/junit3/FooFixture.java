package spec.concordion.integration.junit3;

import org.concordion.integration.junit3.ConcordionTestCase;

public class FooFixture extends ConcordionTestCase {
    public FooFixture() {
        FooFixtureRecorder.setFooFixtureClass(FooFixture.class.getSimpleName());
    }
}
