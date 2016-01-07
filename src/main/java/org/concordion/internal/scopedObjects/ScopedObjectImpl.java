package org.concordion.internal.scopedObjects;

import org.concordion.internal.FieldScope;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectImpl implements ScopedObject {

    private final Class<?> specificationClass;
    private final String fieldName;
    private final FieldScope fieldScope;
    private final ScopedObjectRepository repository;

    public ScopedObjectImpl(Class<?> specificationClass, String fieldName, FieldScope fieldScope, ScopedObjectRepository repository) {
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
