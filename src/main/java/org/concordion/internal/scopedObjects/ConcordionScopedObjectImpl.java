package org.concordion.internal.scopedObjects;

import org.concordion.internal.ConcordionFieldScope;

/**
 * Created by tim on 3/12/15.
 */
public class ConcordionScopedObjectImpl<T> implements ConcordionScopedObject<T> {

    private final Class<? extends T> scopedObjectClass;
    private final ConcordionFieldScope concordionFieldScope;
    private final String name;
    private final Class<?> specificationClass;
    private final ConcordionScopedObjectRepository repository;

    public ConcordionScopedObjectImpl(Class<?> specificationClass, String name, Class<? extends T> scopedObjectClass, ConcordionFieldScope concordionFieldScope, ConcordionScopedObjectRepository repository) {
        this.scopedObjectClass = scopedObjectClass;
        this.concordionFieldScope = concordionFieldScope;
        this.name = name;
        this.specificationClass = specificationClass;
        this.repository = repository;
    }

    public T getObject() throws IllegalAccessException, InstantiationException {
        return repository.getObject(scopedObjectClass, name, concordionFieldScope, specificationClass);
    }

}
