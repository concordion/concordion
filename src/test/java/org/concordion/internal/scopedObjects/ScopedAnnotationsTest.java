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

    @ConcordionScopedField(scope = ConcordionScopedField.Scope.SPECIFICATION)
    private AtomicInteger specificationScopedField;

    @ConcordionScopedField(scope = ConcordionScopedField.Scope.EXAMPLE)
    private AtomicInteger exampleScopedField;

    @ConcordionScopedField(scope = ConcordionScopedField.Scope.GLOBAL)
    private AtomicInteger globalScopedField;

    @ConcordionScopedField
    private AtomicInteger defaultScopedField;


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

        int specificationScopedint = specificationScopedField.addAndGet(1);
        int exampleScopedint = exampleScopedField.addAndGet(1);
        int globalScopedint = globalScopedField.addAndGet(1);
        int defaultScopedint = defaultScopedField.addAndGet(1);

        assertThat(integer, is(equalTo(specificationInt)));
        assertThat(integer, is(equalTo(globalInt)));
        assertThat(integer, is(equalTo(specificationScopedint)));
        assertThat(integer, is(equalTo(globalScopedint)));
        assertThat(1, is(equalTo(exampleInt)));
        assertThat(1, is(equalTo(exampleScopedint)));
        assertThat(1, is(equalTo(defaultScopedint)));
    }
}
