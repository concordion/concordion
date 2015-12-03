package org.concordion.internal.scopedObjects;

import org.concordion.api.ConcordionScopedField;
import org.concordion.api.Fixture;

import java.lang.reflect.Field;

/**
 * Created by tim on 3/12/15.
 */
public enum ConcordionScopedObjectFactory {
    SINGLETON;

    private ConcordionScopedObjectRepository repository = new ConcordionScopedObjectRepository();

    public <T> ConcordionScopedObject<T> create(Class<?> specificationClass,
                                                String name,
                                                Class<? extends T> scopedObject,
                                                ConcordionScopedField.Scope scope) {

        return new ConcordionScopedObjectImpl<T>(specificationClass, name, scopedObject, scope, repository);
    }

    public void setupFixture(Fixture fixture) {

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
                if (field.isAnnotationPresent(ConcordionScopedField.class)) {
                    // got one. Inject the object.
                    injectField(fixtureObject, field);
                }
            }
        }
    }

    private void injectField(Object fixtureObject, Field field)  {
        field.setAccessible(true);

        ConcordionScopedField annotation  = field.getDeclaredAnnotation(ConcordionScopedField.class);
        String name = annotation.value();
        if ("".equals(name)) {
            name = field.getName();
        }
        ConcordionScopedField.Scope scope = annotation.scope();

        ConcordionScopedObject<Object> obj = create(fixtureObject.getClass(), name, field.getType(), scope);

        Object value = null;
        try {
            value = obj.getObject();
            field.set(fixtureObject, value);
        } catch (ReflectiveOperationException e) {
            System.err.println("Could not set value of field " + field.toString() + " due to " + e.getMessage());
        }
    }
}
