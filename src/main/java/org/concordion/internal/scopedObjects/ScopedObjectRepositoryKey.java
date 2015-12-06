package org.concordion.internal.scopedObjects;

import org.concordion.internal.ConcordionScopedField;

import java.util.Arrays;

/**
 * Created by tim on 3/12/15.
 */
class ScopedObjectRepositoryKey<T> {

    private final Class<? extends T> scopedObjectClass;
    private final String name;
    private final ConcordionScopedField.Scope scope;
    private final Class<?> specificationClass;

    public ScopedObjectRepositoryKey(Class<? extends T> scopedObjectClass, String name, ConcordionScopedField.Scope scope, Class<?> specificationClass) {
        this.scopedObjectClass = scopedObjectClass;
        this.name = name;
        this.scope = scope;
        this.specificationClass = specificationClass;
    }

    public Class<? extends T> getScopedObjectClass() {
        return scopedObjectClass;
    }

    @Override
    public int hashCode() {
        switch (scope) {
            case GLOBAL:
                return hash(scopedObjectClass, name);
            default:
                return hash(scopedObjectClass, name, scope, specificationClass);
        }
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }


    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        if (!(other instanceof ScopedObjectRepositoryKey)) {
            return false;
        }

        ScopedObjectRepositoryKey<T> that = (ScopedObjectRepositoryKey<T>) other;

        if (!this.name.equals(that.name)) {
            return false;
        }

        if (!this.scopedObjectClass.equals(that.scopedObjectClass)) {
            return false;
        }

        if (!this.scope.equals(that.scope)) {
            return false;
        }

        // we do not check the scope for globaly scoped objects.
        if (this.scope != ConcordionScopedField.Scope.GLOBAL) {
            if (!this.specificationClass.equals(that.specificationClass)) {
                return false;
            }
        }

        return true;
    }
}
