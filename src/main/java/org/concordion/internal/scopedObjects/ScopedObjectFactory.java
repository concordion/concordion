package org.concordion.internal.scopedObjects;

import org.concordion.api.Scope;

/**
 * Created by tim on 3/12/15.
 */
public enum ScopedObjectFactory {
    SINGLETON;

    private ScopedObjectRepository repository = new ScopedObjectRepository();

    public ScopedObject create(Class<?> specificationClass,
                                                String name,
                                                Scope concordionFieldScope) {

        return new ScopedObjectImpl(specificationClass, name, concordionFieldScope, repository);
    }
}
