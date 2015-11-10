package org.concordion.api;

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
    
    public boolean declaresStatus(ImplementationStatus status) {
        return fixtureClass.isAnnotationPresent(status.getAnnotation());
    }
    
    public boolean declaresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    public boolean declaresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    public boolean declaresResources() {
        return fixtureClass.isAnnotationPresent(Resources.class);
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

    public ImplementationStatus getImplementationStatus() {
        for (ImplementationStatus status : ImplementationStatus.values()) {
            if (declaresStatus(status)) {
                return status;
            }
        }
    
        return ImplementationStatus.EXPECTED_TO_PASS;
    }
}
