package org.concordion.internal.scopedObjects;

/**
 * Created by tim on 14/12/15.
 */
public interface ScopedField {
    void copyValueFromField(Object fixtureObject);
    void copyValueIntoField(Object fixtureObject);
    void destroy(Object fixtureObject);
}
