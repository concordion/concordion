package spec.concordion.integration.junit4;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FooTest {
    public FooTest() {
        FooFixtureRecorder.setFooFixtureClass(FooTest.class.getSimpleName());
    }

    @Test
    public void dummyTest() {
        assertTrue(true);
    }
}
