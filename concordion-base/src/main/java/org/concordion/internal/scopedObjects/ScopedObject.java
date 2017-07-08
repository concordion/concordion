package org.concordion.internal.scopedObjects;

public interface ScopedObject {

    Object getObject() throws IllegalAccessException, InstantiationException;

    void setObject(Object existingValue);
}
