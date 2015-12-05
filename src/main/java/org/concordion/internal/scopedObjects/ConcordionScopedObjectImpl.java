package org.concordion.internal.scopedObjects;

import org.concordion.internal.ConcordionScopedField;

/**
 * Created by tim on 3/12/15.
 */
public class ConcordionScopedObjectImpl<T> implements ConcordionScopedObject<T> {

    private final Class<? extends T> scopedObjectClass;
    private final ConcordionScopedField.Scope scope;
    private final String name;
    private final Class<?> specificationClass;
    private final ConcordionScopedObjectRepository repository;

    public ConcordionScopedObjectImpl(Class<?> specificationClass, String name, Class<? extends T> scopedObjectClass, ConcordionScopedField.Scope scope, ConcordionScopedObjectRepository repository) {
        this.scopedObjectClass = scopedObjectClass;
        this.scope = scope;
        this.name = name;
        this.specificationClass = specificationClass;
        this.repository = repository;
    }

    public T getObject() throws IllegalAccessException, InstantiationException {
        return repository.getObject(scopedObjectClass, name, scope, specificationClass);
    }

}
