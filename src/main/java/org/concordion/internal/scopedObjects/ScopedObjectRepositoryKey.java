package org.concordion.internal.scopedObjects;

import org.concordion.internal.ConcordionFieldScope;

import java.util.Arrays;

/**
 * Created by tim on 3/12/15.
 */
class ScopedObjectRepositoryKey<T> {

    private final Class<? extends T> scopedObjectClass;
    private final String name;
    private final ConcordionFieldScope concordionFieldScope;
    private final Class<?> specificationClass;

    public ScopedObjectRepositoryKey(Class<? extends T> scopedObjectClass, String name, ConcordionFieldScope concordionFieldScope, Class<?> specificationClass) {
        this.scopedObjectClass = scopedObjectClass;
        this.name = name;
        this.concordionFieldScope = concordionFieldScope;
        this.specificationClass = specificationClass;
    }

    public Class<? extends T> getScopedObjectClass() {
        return scopedObjectClass;
    }

    @Override
    public int hashCode() {
        switch (concordionFieldScope) {
            case GLOBAL:
                return hash(scopedObjectClass, name);
            default:
                return hash(scopedObjectClass, name, concordionFieldScope, specificationClass);
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

        if (!this.concordionFieldScope.equals(that.concordionFieldScope)) {
            return false;
        }

        // we do not check the scope for globaly scoped objects.
        if (this.concordionFieldScope != ConcordionFieldScope.GLOBAL) {
            if (!this.specificationClass.equals(that.specificationClass)) {
                return false;
            }
        }

        return true;
    }
}
