package org.concordion.internal.scopedObjects;

import org.junit.rules.TestRule;

/**
 * Created by tim on 3/12/15.
 */
public interface ConcordionScopedObject<T> {

    T getObject() throws IllegalAccessException, InstantiationException;
}
