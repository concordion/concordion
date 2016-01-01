package org.concordion.internal.scopedObjects;

import org.concordion.api.ConcordionScopedField;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;

/**
 * Created by tim on 14/12/15.
 */
public class ConcordionScopedFieldImpl implements ConcordionScopedField {
    private final ConcordionScopedObject<Object> scopedObject;
    private final Field field;

    public ConcordionScopedFieldImpl(ConcordionScopedObject<Object> scopedObject, Field field) {

        this.scopedObject = scopedObject;
        this.field = field;

        verifyField();
    }

    @Override
    public void copyValueFromField(Object fixtureObject) {
        verifyField();

        Object currentValue = null;
        try {
            currentValue = field.get(fixtureObject);
        } catch (IllegalAccessException e) {
            throw new AnnotationFormatError("Could not create object on field " + field.getName());
        }
        scopedObject.setObject(currentValue);
    }

    @Override
    public void copyValueIntoField(Object fixtureObject, boolean replaceIfNotNull) {
        verifyField();

        try {
            Object value = field.get(fixtureObject);
            if (replaceIfNotNull || value == null) {
                value = scopedObject.getObject();
                field.set(fixtureObject, value);
            }
        } catch (InstantiationException e) {
            throw new AnnotationFormatError("Could not create object on field " + field.getName());
        } catch (IllegalAccessException e) {
            throw new AnnotationFormatError("Could not set object on field " + field.getName());
        }
    }

    private void verifyField() {
        if (field.getType().isPrimitive()) {
            throw new AnnotationFormatError("Cannot use concordion scope annotations on primitive types");
        }

        field.setAccessible(true);
    }
}
