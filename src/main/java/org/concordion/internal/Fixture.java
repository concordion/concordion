package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.FailFast;
import org.concordion.api.FullOGNL;
import org.concordion.api.ResultModifier;
import org.concordion.internal.util.Check;

public class Fixture {

    private final Object fixtureObject;
    private Class<?> fixtureClass;

    public Fixture(Object fixtureObject) {
        Check.notNull(fixtureObject, "Fixture is null");
        this.fixtureObject = fixtureObject;
        this.fixtureClass = fixtureObject.getClass();
    }

    public boolean requiresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    public boolean requiresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    public Class<? extends Throwable>[] getFailFastExceptions() {
        FailFast failFastAnnotation = fixtureClass.getAnnotation(FailFast.class);
        Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
        return failFastExceptions;
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

    public String cleanFixtureName() {
        String name = fixtureClass.getSimpleName();
        name = name.replaceAll("Test$", "");
        name = name.replaceAll("Fixture$", "");
        return name;
    }

    public String getDefaultFixtureClassName(String example) {
        String name = cleanFixtureName();
        return Concordion.formatName(String.format("%s#%s", name, example));
    }

    public String getDefaultFixtureClassName() {
        String name = cleanFixtureName();
        return Concordion.formatName(name); // Based on suggestion by Danny Guerrier
    }

    public String getShortenedFixtureName() {
        String dottedClassName = getClassName();
        String slashedClassName = dottedClassName.replaceAll("\\.", "/");
        String specificationName = slashedClassName.replaceAll("(Fixture|Test)$", "");
        return specificationName;
    }

    public boolean hasState(FixtureState state) {
        return getFixtureClass().getAnnotation(state.resultModifier.getAnnotation()) != null;
    }

    public static FixtureState getFixtureState(ResultModifier resultModifier, Fixture fixture) {
        // examples have precedence
        if (resultModifier != null) {
            for (FixtureState state: FixtureState.values()) {
                if (state.getResultModifier() ==  resultModifier) {
                    return state;
                }
            }
        }
    
        // loop through the states
        if (fixture != null) {
            for (FixtureState state : FixtureState.values()) {
                // if we found a match, then return the state
                if (fixture.hasState(state)) {
                    return state;
                }
            }
        }
    
        return FixtureState.EXPECTED_TO_PASS;
    }
}
