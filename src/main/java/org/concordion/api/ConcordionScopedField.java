package org.concordion.api;

/**
 * Created by tim on 14/12/15.
 */
public interface ConcordionScopedField {
    void copyValueFromField(Object fixtureObject);
    void copyValueIntoField(Object fixtureObject, boolean replaceIfNotNull);
}
