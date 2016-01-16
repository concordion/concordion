package org.concordion.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.option.ConcordionOptions;

public class FixtureType implements FixtureOptions {

    protected Class<?> fixtureClass;
    private ArrayList<Class<?>> classHierarchyParentFirst;
    
    public FixtureType(Class<?> fixtureClass) {
        this.fixtureClass = fixtureClass;
    }

    @Override
    public boolean declaresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    @Override
    public boolean declaresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    @Override
    public boolean declaresResources() {
        return fixtureClass.isAnnotationPresent(ConcordionResources.class);
    }

    @Override
    public Class<? extends Throwable>[] getDeclaredFailFastExceptions() {
        FailFast failFastAnnotation = fixtureClass.getAnnotation(FailFast.class);
        Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
        return failFastExceptions;
    }

    public boolean declaresStatus(ImplementationStatus status) {
        return fixtureClass.isAnnotationPresent(status.getAnnotation());
    }

    @Override
    public ImplementationStatus getDeclaredImplementationStatus() {
        for (ImplementationStatus status : ImplementationStatus.values()) {
            if (declaresStatus(status)) {
                return status;
            }
        }
    
        return ImplementationStatus.EXPECTED_TO_PASS;
    }

    /**
     * Returns the fixture class and all of its superclasses, excluding java.lang.Object,
     * ordered from the most super class to the fixture class.
     */
    protected List<Class<?>> getClassHierarchyParentFirst() {
        if (classHierarchyParentFirst != null) {
            return classHierarchyParentFirst;
        }
        
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> current = fixtureClass;
        while (current != null && current != Object.class) {
            classes.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(classes);
        this.classHierarchyParentFirst = classes;
        return classes;
    }

    @Override
    public List<ConcordionOptions> getDeclaredConcordionOptionsParentFirst() {
        List<ConcordionOptions> annotations = new ArrayList<ConcordionOptions>();
        for (Class<?> class1 : getClassHierarchyParentFirst()) {
            ConcordionOptions annotation = class1.getAnnotation(ConcordionOptions.class);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }
}
