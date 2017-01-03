package spec.concordion.integration.junit3;

import junit.framework.AssertionFailedError;

public class FooFixtureRecorder {
    private static String fooFixtureClass;

    public static String getFooFixtureClass() {
        return fooFixtureClass;
    }

    public static void setFooFixtureClass(String className) {
        if (fooFixtureClass != null) {
            throw new AssertionFailedError("Only expected one test class to be invoked");
        }
        fooFixtureClass = className;
    }
}
