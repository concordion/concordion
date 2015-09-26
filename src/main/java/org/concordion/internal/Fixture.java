package org.concordion.internal;

import org.concordion.api.FailFast;
import org.concordion.api.FullOGNL;
import org.concordion.internal.util.Check;

public class Fixture {
    private final Object fixtureObject;
    private Class<?> fixtureClass;

    public Fixture(Object fixtureObject) {
        Check.notNull(fixtureObject, "Fixture is null");
        this.fixtureObject = fixtureObject;
        this.fixtureClass = fixtureObject.getClass();
    }

    public String getClassName() {
        return fixtureClass.getName();
    }
    
    public Object getFixtureObject() {
        return fixtureObject;
    }
    
    public Class<? extends Object> getFixtureClass() {
        return fixtureClass;
    }
    
    public boolean declaresState(ExpectedState state) {
        return fixtureClass.isAnnotationPresent(state.resultModifier.getAnnotation());
    }
    
    public boolean declaresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    public boolean declaresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    public Class<? extends Throwable>[] getFailFastExceptions() {
        FailFast failFastAnnotation = fixtureClass.getAnnotation(FailFast.class);
        Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
        return failFastExceptions;
    }

    public String getDescription() {
        String name = removeSuffix(fixtureClass.getSimpleName());
        return String.format("[Concordion Specification for '%s']", name); // Based on suggestion by Danny Guerrier
    }

    public String getFixturePathWithoutSuffix() {
        String slashedClassName = getClassName().replaceAll("\\.", "/");
        return removeSuffix(slashedClassName);
    }

    private String removeSuffix(String fixtureName) {
        return fixtureName.replaceAll("(Fixture|Test)$", "");
    }

    public ExpectedState getExpectedState() {
        for (ExpectedState state : ExpectedState.values()) {
            if (declaresState(state)) {
                return state;
            }
        }
    
        return ExpectedState.EXPECTED_TO_PASS;
    }
}
