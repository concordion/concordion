package org.concordion.internal.scopedObjects;

import org.concordion.api.ConcordionScopedField;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectRepositoryKeyTest {

    @Test
    public void testEqualsSpecScope() {

        ScopedObjectRepositoryKey<Object> key = new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.SPECIFICATION, getClass());

        checkEqualsHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(Object.class, "name", ConcordionScopedField.Scope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name2", ConcordionScopedField.Scope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.SPECIFICATION, Object.class));

    }

    private void checkEqualsHashCode(ScopedObjectRepositoryKey<Object> one, ScopedObjectRepositoryKey<Object> two) {
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void testEqualsGlobalScope() {

        ScopedObjectRepositoryKey<Object> key = new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.GLOBAL, getClass());

        checkEqualsHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(Object.class, "name", ConcordionScopedField.Scope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name2", ConcordionScopedField.Scope.GLOBAL, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.SPECIFICATION, getClass()));
        // should be true - globally scoped objects ignore the spec class in their comparrison.
        checkEqualsHashCode(key, new ScopedObjectRepositoryKey<Object>(getClass(), "name", ConcordionScopedField.Scope.GLOBAL, Object.class));

    }

    private void checkNotEqualsNotHashCode(ScopedObjectRepositoryKey<Object> key, ScopedObjectRepositoryKey<Object> name) {
        assertNotEquals(key, name);

        // not a big thing really if this fails, but it's nice not to have hash collisions in test data!
        assertNotEquals(key.hashCode(), name.hashCode());
    }

}