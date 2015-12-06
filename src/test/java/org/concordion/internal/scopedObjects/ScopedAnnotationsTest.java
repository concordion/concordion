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
public class ScopedAnnotationsTest {

    @SpecificationScoped
    AtomicInteger specificationInteger;

    @ExampleScoped
    AtomicInteger exampleInteger;

    @GloballyScoped
    AtomicInteger globalInteger;


    private static int integer = 0;

    @Before
    public void before() {
        Fixture fixture = new Fixture(this);

        ConcordionScopedObjectFactory.SINGLETON.setupFixture(fixture);
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

        int specificationInt = specificationInteger.addAndGet(1);
        int exampleInt = exampleInteger.addAndGet(1);
        int globalInt = globalInteger.addAndGet(1);

        assertThat(integer, is(equalTo(specificationInt)));
        assertThat(integer, is(equalTo(globalInt)));
        assertThat(1, is(equalTo(exampleInt)));
    }
}
