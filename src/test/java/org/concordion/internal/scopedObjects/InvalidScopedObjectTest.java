package org.concordion.internal.scopedObjects;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.lang.annotation.AnnotationFormatError;

import org.concordion.api.ConcordionScoped;
import org.concordion.api.Scope;
import org.concordion.internal.FixtureInstance;
import org.junit.Test;

public class InvalidScopedObjectTest {

    @ConcordionScoped(Scope.SPECIFICATION)
    Integer x;

    @Test
    public void before() {
        try {
            new FixtureInstance(this);
            fail("Expected AnnotationFormatError since only types of ScopedObjectHolder can be annotated with ConcordionScoped");
        } catch (AnnotationFormatError e) {
            assertThat(e.getMessage(), containsString("The 'ConcordionScoped' annotation can only be applied to fields of type 'ScopedObjectHolder'"));
        }
    }
}
