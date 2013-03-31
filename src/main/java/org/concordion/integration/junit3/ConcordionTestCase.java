package org.concordion.integration.junit3;

import junit.framework.TestCase;

import org.concordion.internal.FixtureRunner;

public abstract class ConcordionTestCase extends TestCase {

    public void testProcessSpecification() throws Throwable {
        new FixtureRunner().run(this);
    }
}
