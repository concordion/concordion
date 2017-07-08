package org.concordion.internal.scopedObjects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.ConcordionScoped;
import org.concordion.api.Fixture;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.api.Scope;
import org.concordion.internal.FixtureInstance;
import org.junit.Before;
import org.junit.Test;

public class ScopedFieldsTest {

    @ConcordionScoped(Scope.SPECIFICATION)
    ScopedObjectHolder<AtomicInteger> specificationInteger = new ScopedObjectHolder<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }
    };
            
    @ConcordionScoped(Scope.EXAMPLE)
    ScopedObjectHolder<AtomicInteger> exampleInteger = new ScopedObjectHolder<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }
    };

    private static boolean firstRun = true;
            
    private static int integer = 0;

    @Before
    public void before() {
        Fixture fixture = new FixtureInstance(this);
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
