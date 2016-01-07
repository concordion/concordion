package org.concordion.internal.scopedObjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.concordion.internal.FieldScope;
import org.concordion.internal.scopedObjects.ScopedObjectRepository.ScopedObjectRepositoryKey;
import org.junit.Test;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectRepositoryKeyTest {

    @Test
    public void testEqualsSpecScope() {

        ScopedObjectRepositoryKey key = new ScopedObjectRepositoryKey("name", FieldScope.SPECIFICATION, getClass());

        checkEqualsHashCode(key, new ScopedObjectRepositoryKey("name", FieldScope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey("name", FieldScope.EXAMPLE, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey("name2", FieldScope.SPECIFICATION, getClass()));
        checkNotEqualsNotHashCode(key, new ScopedObjectRepositoryKey("name", FieldScope.SPECIFICATION, Object.class));
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