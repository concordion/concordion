package org.concordion.integration.junit3;

import junit.framework.TestCase;

import org.concordion.api.Fixture;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;

/**
 * @deprecated As of release 2.0, JUnit 3 support is deprecated. 
 * With JUnit 5 on the horizon, we will be focussing on ongoing support for JUnit 4 and 5.
 * Please updated your test cases to use the 
 * <a href="http://concordion.github.io/concordion/latest/spec/integration/junit4/Junit4.html">JUnit 4</a> support.
 */
@Deprecated
public abstract class ConcordionTestCase extends TestCase {

    public void testProcessSpecification() throws Throwable {
        Fixture fixture  = new FixtureInstance(this);
        fixture.beforeSpecification();
        fixture.setupForRun(this);
        new FixtureRunner(fixture).run(fixture);
        fixture.afterSpecification();
    }
}
