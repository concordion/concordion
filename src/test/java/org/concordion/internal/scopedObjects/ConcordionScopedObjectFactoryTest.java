package org.concordion.internal.scopedObjects;

import org.concordion.api.ConcordionScopedField;
import org.concordion.api.Fixture;
import org.concordion.internal.scopedObjects.ConcordionScopedObject;
import org.concordion.internal.scopedObjects.ConcordionScopedObjectFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by tim on 3/12/15.
 */
public class ConcordionScopedObjectFactoryTest {

    private static int staticValue = 0;

    private ConcordionScopedObject<AtomicInteger> scopedValue =
            ConcordionScopedObjectFactory.SINGLETON.create(
                    getClass(),
                    "variable name",
                    AtomicInteger.class,
                    ConcordionScopedField.Scope.SPECIFICATION);


    private int nonStaticValue = 0;

    @Before
    public void before() {
        // apply any annotations
        Fixture fixture = new Fixture(this);
        ConcordionScopedObjectFactory.SINGLETON.setupFixture(fixture);
    }

    @Test
    public void test1() throws InstantiationException, IllegalAccessException {
        testIntegerValues();
    }

    @Test
    public void test2() throws InstantiationException, IllegalAccessException {
        testIntegerValues();
    }

    private synchronized void testIntegerValues() throws IllegalAccessException, InstantiationException {

        // increment all the counters
        staticValue++;
        nonStaticValue++;
        int scopedVal = scopedValue.getObject().addAndGet(1);

        // if our scoped value is acting like a static variable, then it should have the same value as the static
        // variable
        assertEquals(staticValue, scopedVal);

        // just to check that jUnit is creating a new instance of this class for each test - we check the non static
        // value is 1.
        assertEquals(nonStaticValue, 1);
    }
}