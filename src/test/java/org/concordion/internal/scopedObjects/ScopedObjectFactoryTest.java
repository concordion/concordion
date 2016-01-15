package org.concordion.internal.scopedObjects;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.Scope;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectFactoryTest {

    private static int staticValue = 0;

    private ScopedObject scopedValue =
            ScopedObjectFactory.SINGLETON.create(
                    getClass(),
                    "variable name",
                    Scope.SPECIFICATION);


    private int nonStaticValue = 0;

    @Before
    public void before() throws IllegalAccessException, InstantiationException {
        if (scopedValue.getObject() == null) {
            scopedValue.setObject(new AtomicInteger());
        }
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
        int scopedVal = ((AtomicInteger) scopedValue.getObject()).addAndGet(1);

        // if our scoped value is acting like a static variable, then it should have the same value as the static
        // variable
        assertEquals(staticValue, scopedVal);

        // just to check that jUnit is creating a new instance of this class for each test - we check the non static
        // value is 1.
        assertEquals(nonStaticValue, 1);
    }
}