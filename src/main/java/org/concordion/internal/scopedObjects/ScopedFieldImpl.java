package org.concordion.internal.scopedObjects;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.concordion.api.ScopedObjectHolder;

public class ScopedFieldImpl implements ScopedField {
    private final ScopedObject scopedObject;
    private final Field field;

    public ScopedFieldImpl(ScopedObject scopedObject, Field field) {
        this.scopedObject = scopedObject;
        this.field = field;
    }

    @Override
    public void copyValueFromField(Object fixtureObject) {
        field.setAccessible(true);

        Object currentValue = null;
        try {
            currentValue = field.get(fixtureObject);
        } catch (IllegalAccessException e) {
            throw new AnnotationFormatError("Could not get object on field " + field.getName());
        }
        scopedObject.setObject(currentValue);
    }

    @Override
    public void copyValueIntoField(Object fixtureObject) {
        field.setAccessible(true);

        try {
            Object value = scopedObject.getObject();
            field.set(fixtureObject, value);
        } catch (InstantiationException e) {
            throw new AnnotationFormatError("Could not create object on field " + field.getName());
        } catch (IllegalAccessException e) {
            throw new AnnotationFormatError("Could not set object on field " + field.getName());
        }
    }

    @Override
    public void destroy(Object fixtureObject) {
        field.setAccessible(true);

        Method destroy;
        try {
            Object fieldObject = field.get(fixtureObject);
            
            Class<?> scopedClass = fieldObject.getClass().getSuperclass();
            while (!(ScopedObjectHolder.class.equals(scopedClass))) {
                scopedClass = scopedClass.getSuperclass();
                if (scopedClass == null) {
                    return;
                }
            }
            Field valueField = scopedClass.getDeclaredField("value");
            valueField.setAccessible(true);
            Object value = valueField.get(fieldObject);
            if (value != null) {
                destroy = scopedClass.getDeclaredMethod("destroy", Object.class);
                destroy.setAccessible(true);
                destroy.invoke(fieldObject, value);
            }
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException)e.getCause();
            }
            throw new RuntimeException(e.getCause());
        } catch (Exception e) {
            String msg = String.format("Unexpected exception while destroying scoped object '%s' on class '%s'", field.getName(), fixtureObject.getClass().getName());
            throw new RuntimeException(msg, e);
        }
    }
}
