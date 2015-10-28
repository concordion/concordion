package org.concordion.internal;

import org.junit.Test;

import static org.junit.Assert.*;

public class MultiPatternTest {

    private MultiPattern testingInstance = MultiPattern.fromRegularExpressions(
            "[a-z]",
            "[0-9]"
    );

    @Test
    public void testMatchesFirstRegex() {
        assertTrue(testingInstance.matches("a"));
    }

    @Test
    public void testMatchesSecondRegex() {
        assertTrue(testingInstance.matches("0"));
    }

    @Test
    public void testDoesNotMatch() {
        assertFalse(testingInstance.matches("A"));
    }
}