package org.concordion.internal.scopedObjects;

import org.concordion.internal.ConcordionFieldScope;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectRepositoryKeyTest {

    @Test
    public void testEqualsSpecScope() {

        ScopedObjectRepositoryKey<Object> key = new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.SPECIFICATION, getClass());

        checkEqualsHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(Object.class, "name", ConcordionFieldScope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name2", ConcordionFieldScope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.SPECIFICATION, Object.class));

    }

    private void checkEqualsHashCode(ScopedObjectRepositoryKey<Object> one, ScopedObjectRepositoryKey<Object> two) {
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void testEqualsGlobalScope() {

        ScopedObjectRepositoryKey<Object> key = new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.GLOBAL, getClass());

        checkEqualsHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(Object.class, "name", ConcordionFieldScope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name2", ConcordionFieldScope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.SPECIFICATION, getClass()));
        // should be true - globally scoped objects ignore the spec class in their comparrison.
        checkEqualsHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionFieldScope.GLOBAL, Object.class));

    }

    private void checkNotEqualsNotHashCode(ScopedObjectRepositoryKey<Object> key, ScopedObjectRepositoryKey<Object> name) {
        assertNotEquals(key, name);

        // not a big thing really if this fails, but it's nice not to have hash collisions in test data!
        assertNotEquals(key.hashCode(), name.hashCode());
    }

}