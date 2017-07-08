package org.concordion.api;

/**
 * A factory and holder for objects that are to have their scope managed by Concordion.
 * <p>
 * When the {@link #get()} method is invoked, the wrapped object is lazily constructed by
 * calling the {@link #create()} method.
 * </p>
 * <p>
 * When annotated with {@link ConcordionScoped}, the scope will be managed by Concordion and the
 * object destroyed when it goes out of scope.
 * </p>
 * <p><b>For example:</b></p>
 * <pre>
 * &#064;ConcordionScoped(Scope.SPECIFICATION)
 * private ScopedObjectHolder&lt;Browser&gt; browserHolder = new ScopedObjectHolder&lt;Browser&gt;() {
 *     &#064;Override
 *     protected Browser create() {
 *         return new Browser();
 *     }
 *
 *     &#064;Override
 *     protected void destroy(Browser browser) {
 *         browser.close();
 *     };
 * };
 * </pre>
 * <p>
 * The browser will be constructed in the first method that calls <code>browserHolder.get()</code>.
 * </p>
 * <p>
 * The browser will be destroyed when the specification completes.
 * </p>
 *
 * @since 2.0.0
 *
 * @param <T> type of wrapped object
 */
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

    /**
     * Returns a value that indicates whether the current scoped object has been created.
     * @return <code>true</code> if the scoped object has been created (by calling {@link #get()}),
     * <code>false</code> otherwise.
     */
    public boolean isCreated() {
        return value != null;
    }
}
