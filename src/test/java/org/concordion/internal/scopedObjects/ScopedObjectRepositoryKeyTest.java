package org.concordion.internal.scopedObjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.concordion.api.Scope;
import org.concordion.internal.scopedObjects.ScopedObjectRepository.ScopedObjectRepositoryKey;
import org.junit.Test;

public class ScopedObjectRepositoryKeyTest {

    @Test
    public void testEqualsSpecScope() {

        ScopedObjectRepositoryKey key = new ScopedObjectRepositoryKey("name", Scope.SPECIFICATION, getClass());

        checkEqualsHashCode(key, new ScopedObjectRepositoryKey("name", Scope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey("name", Scope.EXAMPLE, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey("name2", Scope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey("name", Scope.SPECIFICATION, Object.class));
    }

    private void checkEqualsHashCode(ScopedObjectRepositoryKey one, ScopedObjectRepositoryKey two) {
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }

    private void checkNotEqualsNotHashCode(Object one, Object two) {
        assertNotEquals(one, two);

        // not a big thing really if this fails, but it's nice not to have hash collisions in test data!
        assertNotEquals(one.hashCode(), two.hashCode());
    }

}