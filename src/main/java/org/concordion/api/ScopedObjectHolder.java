package org.concordion.api;

public abstract class ScopedObjectHolder<T> {

    private volatile T value;

    protected abstract T create();

    protected void destroy(T t) {
    }

    /**
     * Return the value of the variable. 
     * Uses lazy initialisation to call the {@link #create()} method (implemented safely for Java 5.0+, see
     * http://www.oracle.com/technetwork/articles/javase/bloch-effective-08-qa-140880.html).
     * @return value of scoped variable
     */
    public T get() {
        T result = value;
        if (result == null) { // First check (no locking)
            synchronized (this) {
                result = value;
                if (result == null) { // Second check (with locking)
                    value = result = create();
                }
            }
        }
        return result;
    }
}
