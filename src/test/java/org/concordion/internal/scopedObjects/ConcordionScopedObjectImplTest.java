package org.concordion.internal.scopedObjects;

import org.concordion.api.ConcordionScopedField;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tim on 3/12/15.
 */
public class ConcordionScopedObjectImplTest {

    private ConcordionScopedObjectRepository repo = new ConcordionScopedObjectRepository();

    @Test
    public void testScopedObjectsMatch() throws InstantiationException, IllegalAccessException {

        // setup
        ConcordionScopedObjectImpl<String> scopedObj1 = new ConcordionScopedObjectImpl<String>(getClass(), "name", String.class, ConcordionScopedField.Scope.SPECIFICATION, repo);
        ConcordionScopedObjectImpl<String> scopedObj2 = new ConcordionScopedObjectImpl<String>(getClass(), "name", String.class, ConcordionScopedField.Scope.SPECIFICATION, repo);

        // test
        String obj1 = scopedObj1.getObject();
        String obj2 = scopedObj2.getObject();

        // asserts
        // assert that the references are the same
        assertNotNull(obj1);
        assertTrue(obj1 == obj2);
    }
    @Test
    public void testScopedObjectsDifferentNamesNoMatch() throws InstantiationException, IllegalAccessException {

        // setup
        ConcordionScopedObjectImpl<String> scopedObj1 = new ConcordionScopedObjectImpl<String>(getClass(), "name1", String.class, ConcordionScopedField.Scope.SPECIFICATION, repo);
        ConcordionScopedObjectImpl<String> scopedObj2 = new ConcordionScopedObjectImpl<String>(getClass(), "name2", String.class, ConcordionScopedField.Scope.SPECIFICATION, repo);

        // test
        String obj1 = scopedObj1.getObject();
        String obj2 = scopedObj2.getObject();

        // asserts
        // assert that the references are different
        assertNotNull(obj1);
        assertTrue(obj1 != obj2);
    }

}