package org.concordion.internal.scopedObjects;

import org.concordion.api.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by tim on 5/12/15.
 */
public class ScopedFieldsTest {

    @SpecificationScoped
    Scoped<AtomicInteger> specificationInteger = new Scoped<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }
    };
            
    @ExampleScoped
    Scoped<AtomicInteger> exampleInteger = new Scoped<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }
    };

    private static boolean firstRun = true;
            
    private static int integer = 0;

    @Before
    public void before() {
        Fixture fixture = new Fixture(this);
        if (firstRun) {
            firstRun = false;
            fixture.beforeSpecification();
        }
        fixture.setupForRun(this);
    }

    @Test
    public void test1() {
        runTest();
    }

    @Test
    public void test2() {
        runTest();
    }

    public void runTest() {
        integer++;

        int specificationInt = specificationInteger.get().addAndGet(1);
        int exampleInt = exampleInteger.get().addAndGet(1);

        assertThat(integer, is(equalTo(specificationInt)));
        assertThat(1, is(equalTo(exampleInt)));
    }
}
