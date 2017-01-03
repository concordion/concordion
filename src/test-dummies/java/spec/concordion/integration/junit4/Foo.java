package spec.concordion.integration.junit4;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Foo {
    public Foo() {
        FooFixtureRecorder.setFooFixtureClass(Foo.class.getSimpleName());
    }

    @Test
    public void dummyTest() {
        assertTrue(true);
    }
}
