package org.concordion.internal;

import org.concordion.api.FailFast;
import org.concordion.api.FullOGNL;
import org.concordion.internal.util.Check;

public class Fixture {

    private final Object fixtureObject;
    private Class<?> fixtureClass;

    // TODO create NullFixture for tests (and find out why it's needed!)
    public Fixture(Object fixtureObject) {
        this.fixtureObject = fixtureObject;
        if (fixtureObject != null) {
            this.fixtureClass = fixtureObject.getClass();
        }
    }

    boolean requiresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    boolean requiresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    Class<? extends Throwable>[] getFailFastExceptions() {
        FailFast failFastAnnotation = fixtureClass.getAnnotation(FailFast.class);
        Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
        return failFastExceptions;
    }

    String getClassName() {
        return fixtureClass.getName();
    }

    void checkNotNull() {
        Check.notNull(getFixtureObject(), "Fixture is null");
    }

    public Object getFixtureObject() {
        return fixtureObject;
    }

    public Class<? extends Object> getFixtureClass() {
        return fixtureClass;
    }
}
