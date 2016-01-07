package org.concordion.internal.scopedObjects;

/**
 * Created by tim on 3/12/15.
 */
public interface ScopedObject {

    Object getObject() throws IllegalAccessException, InstantiationException;

    void setObject(Object existingValue);
}
