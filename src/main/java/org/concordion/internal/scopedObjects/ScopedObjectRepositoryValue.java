package org.concordion.internal.scopedObjects;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectRepositoryValue<T> {
    private final ScopedObjectRepositoryKey key;
    private final T value;

    public ScopedObjectRepositoryValue(ScopedObjectRepositoryKey key) throws IllegalAccessException, InstantiationException {
        this.key = key;
        this.value = (T) key.getScopedObjectClass().newInstance();
    }

    public synchronized T getObject()  {
        return value;
    }
}
