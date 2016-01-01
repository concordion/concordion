package org.concordion.internal.scopedObjects;

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
}
