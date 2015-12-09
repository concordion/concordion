package org.concordion.internal.scopedObjects;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;

import org.concordion.api.Fixture;
import org.concordion.internal.ConcordionScopeDeclaration;
import org.concordion.internal.ConcordionFieldScope;

/**
 * Created by tim on 3/12/15.
 */
public enum ConcordionScopedObjectFactory {
    SINGLETON;

    private ConcordionScopedObjectRepository repository = new ConcordionScopedObjectRepository();

    public <T> ConcordionScopedObject<T> create(Class<?> specificationClass,
                                                String name,
                                                Class<? extends T> scopedObject,
                                                ConcordionFieldScope concordionFieldScope) {

        return new ConcordionScopedObjectImpl<T>(specificationClass, name, scopedObject, concordionFieldScope, repository);
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

        // stop when we get to the base of the hierarchy
        if (fixtureClass == Object.class) {
            return;
        }

        // do the superclass first.
        injectScopedObjects(fixtureObject, fixtureClass.getSuperclass());

        // search for a field annotated with ConcordionScopedValue
        Field[] fields = fixtureClass.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                ConcordionScopedObject<Object> scopedObject = getScopedObject(fixtureObject, field);
                
                if (scopedObject != null) {
                    injectField(fixtureObject, field, scopedObject);
                }
            }
        }
    }

    private ConcordionScopedObject<Object> getScopedObject(Object fixtureObject, Field field) {

        String name = "";
        ConcordionFieldScope concordionFieldScope = null;

        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            ConcordionFieldScope annotationConcordionFieldScope = getScopeFromAnnotation(annotation);
            if (annotationConcordionFieldScope != null) {
                if (concordionFieldScope != null) {
                    throw new AnnotationFormatError("Multiple concordion scope annotations on field '" + field.getName() + "'");
                }
                concordionFieldScope = annotationConcordionFieldScope;
                try {
                    name = (String) annotation.getClass().getDeclaredMethod("value").invoke(annotation);
                } catch (Exception e) {
                    throw new AnnotationFormatError("Expected concordion scope annotation on field '"  + field.getName() + "' to also have a 'value()' method");
                }
            }
        }

        if (concordionFieldScope == null) {
            return null;
        }

        // use the field name if not set.
        if ("".equals(name)) {
            name = field.getName();
        }

        return create(fixtureObject.getClass(), name, field.getType(), concordionFieldScope);
    }

    private ConcordionFieldScope getScopeFromAnnotation(Annotation annotation) {
        ConcordionScopeDeclaration scopeDeclaration = annotation.annotationType().getAnnotation(ConcordionScopeDeclaration.class);
        ConcordionFieldScope concordionFieldScope = null;
        if (scopeDeclaration != null) {
            concordionFieldScope = scopeDeclaration.scope();
        }
        return concordionFieldScope;
    }

    private void injectField(Object fixtureObject, Field field, ConcordionScopedObject<Object> obj)  {

        if (field.getType().isPrimitive()) {
            throw new AnnotationFormatError("Cannot use concordion scope annotations on primitive types");
        }

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
}
