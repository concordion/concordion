package org.concordion.api;

public abstract class Scoped<T> {

    private T value;

    protected abstract T create();

    protected void destroy(T t) {
    }

    public T get() {
        if (value == null) {
            value = create();
        }
        return value;
    }
}
