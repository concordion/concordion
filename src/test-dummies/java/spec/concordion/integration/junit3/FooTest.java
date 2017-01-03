package spec.concordion.integration.junit3;

import junit.framework.TestCase;

public class FooTest extends TestCase {
    public FooTest() {
        FooFixtureRecorder.setFooFixtureClass(FooTest.class.getSimpleName());
    }

    public void testDummy() {
        assertTrue(true);
    }
}
