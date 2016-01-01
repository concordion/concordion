package org.concordion.internal.scopedObjects;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectRepositoryValue<T> {
    private final ScopedObjectRepositoryKey key;
    private T value;

    public ScopedObjectRepositoryValue(ScopedObjectRepositoryKey key) throws IllegalAccessException, InstantiationException {
        this(key, (T) key.getScopedObjectClass().newInstance());
    }

    public ScopedObjectRepositoryValue(ScopedObjectRepositoryKey key, T value) {
        this.key = key;
        this.value = value;
    }

    public void setObject(T value) {
        this.value = value;
    }

    public T getObject()  {
        return value;
    }
}
