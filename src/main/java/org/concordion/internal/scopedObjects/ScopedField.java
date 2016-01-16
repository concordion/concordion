package org.concordion.internal.scopedObjects;

public interface ScopedField {
    void saveValueFromField(Object fixtureObject);
    void loadValueIntoField(Object fixtureObject);
    void destroy(Object fixtureObject);
}
