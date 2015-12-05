package org.concordion.internal.scopedObjects;

import org.concordion.Concordion;
import org.concordion.api.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;

/**
 * Created by tim on 3/12/15.
 */
public enum ConcordionScopedObjectFactory {
    SINGLETON;

    private ConcordionScopedObjectRepository repository = new ConcordionScopedObjectRepository();
    public static final Class<?>[] SCOPE_ANNOTATIONS = new Class<?>[]{
            ConcordionScopedField.class,
            ExampleScoped.class,
            SpecificationScoped.class,
            GloballyScoped.class
    };

    public <T> ConcordionScopedObject<T> create(Class<?> specificationClass,
                                                String name,
                                                Class<? extends T> scopedObject,
                                                ConcordionScopedField.Scope scope) {

        return new ConcordionScopedObjectImpl<T>(specificationClass, name, scopedObject, scope, repository);
    }

    /**
     *
     * Used to set the values for all annotations in the fixture.
     *
     * @param fixture the fixture to set
     * @throws AnnotationFormatError if there is an annotation processing error or introspection error.
     */
    public void setupFixture(Fixture fixture) throws AnnotationFormatError{

        injectScopedObjects(fixture.getFixtureObject(), fixture.getFixtureClass());

    }

    private void injectScopedObjects(Object fixtureObject, Class<?> fixtureClass)  {

        // stop when we get to the base of the heirarchy
        if (fixtureClass == Object.class) {
            return;
        }

        // do the superclass first.
        injectScopedObjects(fixtureObject, fixtureClass.getSuperclass());

        // search for a field annotated with ConcordionScopedValue
        Field[] fields = fixtureClass.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                // got one. Inject the object.
                int numConcordionAnnotations = countAnnotations(field);
                if (numConcordionAnnotations == 1) {
                    // no annotations
                    injectField(fixtureObject, field, getScopedObject(fixtureObject, field));
                } else if (numConcordionAnnotations > 1) {
                    if (numConcordionAnnotations > 1) {
                        throw new AnnotationFormatError("multiple concordion annotations on field " + field.getName());
                    }
                }

            }
        }
    }

    private ConcordionScopedObject<Object> getScopedObject(Object fixtureObject, Field field) {

        ConcordionScopedField scopedField = field.getAnnotation(ConcordionScopedField.class);
        ExampleScoped exampleScope = field.getAnnotation(ExampleScoped.class);
        SpecificationScoped specificationScoped = field.getAnnotation(SpecificationScoped.class);
        GloballyScoped globalScoped = field.getAnnotation(GloballyScoped.class);

        String name = "";
        ConcordionScopedField.Scope scope = null;

        if (scopedField != null) {
            name = scopedField.value();
            scope = scopedField.scope();

        } else if (exampleScope != null) {
            name = exampleScope.value();
            scope = ConcordionScopedField.Scope.EXAMPLE;

        } else if (specificationScoped != null) {
            name = specificationScoped.value();
            scope = ConcordionScopedField.Scope.SPECIFICATION;

        } else if (globalScoped != null) {
            name = globalScoped.value();
            scope = ConcordionScopedField.Scope.GLOBAL;
        }

        // use the field name if not set.
        if ("".equals(name)) {
            name = field.getName();
        }

        return create(fixtureObject.getClass(), name, field.getType(), scope);
    }

    private void injectField(Object fixtureObject, Field field, ConcordionScopedObject<Object> obj)  {
        field.setAccessible(true);

        Object value = null;
        try {
            value = obj.getObject();
            field.set(fixtureObject, value);
        } catch (InstantiationException e) {
            throw new AnnotationFormatError("Could not create object on field " + field.getName());
        } catch (IllegalAccessException e) {
            throw new AnnotationFormatError("Could not set object on field " + field.getName());
        }
    }

    private int countAnnotations(Field field) {
        int count = 0;
        for (Class<?> annotation: this.SCOPE_ANNOTATIONS) {
            if (field.getAnnotation((Class<? extends Annotation>)annotation) != null) {
                count++;
            }
        }
        return count;
    }

}
