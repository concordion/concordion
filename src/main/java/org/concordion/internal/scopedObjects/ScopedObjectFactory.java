package org.concordion.internal.scopedObjects;

import org.concordion.api.Scope;

public enum ScopedObjectFactory {
    SINGLETON;

    private ScopedObjectRepository repository = new ScopedObjectRepository();

    public ScopedObject create(Class<?> specificationClass,
                                                String name,
                                                Scope concordionFieldScope) {

        return new ScopedObjectImpl(specificationClass, name, concordionFieldScope, repository);
    }
}
