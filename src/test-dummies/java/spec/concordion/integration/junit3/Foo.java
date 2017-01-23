package spec.concordion.integration.junit3;

import junit.framework.TestCase;

public class Foo extends TestCase {
    public Foo() {
        FooFixtureRecorder.setFooFixtureClass(Foo.class.getSimpleName());
    }

    public void testDummy() {
        assertTrue(true);
    }
}
