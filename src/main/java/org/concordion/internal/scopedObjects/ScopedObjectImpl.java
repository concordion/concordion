package org.concordion.internal.scopedObjects;

import org.concordion.internal.ScopeType;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectImpl implements ScopedObject {

    private final Class<?> specificationClass;
    private final String fieldName;
    private final ScopeType fieldScope;
    private final ScopedObjectRepository repository;

    public ScopedObjectImpl(Class<?> specificationClass, String fieldName, ScopeType fieldScope, ScopedObjectRepository repository) {
        this.specificationClass = specificationClass;
        this.fieldName = fieldName;
        this.fieldScope = fieldScope;
        this.repository = repository;
    }

    @Override
    public Object getObject() throws IllegalAccessException, InstantiationException {
        return repository.getObject(fieldName, fieldScope, specificationClass);
    }

    @Override
    public void setObject(Object existingValue) {
        repository.setObject(fieldName, fieldScope, specificationClass, existingValue);
    }
}
