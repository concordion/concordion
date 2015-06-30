package test.concordion.internal.runner;

import org.concordion.internal.cache.CachedRunResults;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by TimW5 on 27/09/14.
 *
 * @author TimW5
 */
public class CachedResultsUnitTest {

    private CachedRunResults runResults = CachedRunResults.SINGLETON;

    @Before
    public void before() {
        runResults.clearCache();
    }

    @Test
    public void testCacheInProgress() {

        assertNull(runResults.startRun(this.getClass(), null));
        assertNotNull(runResults.startRun(this.getClass(), null));

    }

    @Test
    public void testCacheInProgressWithExample() {

        assertNull(runResults.startRun(this.getClass(), "eg"));
        assertNotNull(runResults.startRun(this.getClass(), "eg"));

    }

    private static class JustAnotherClass {};

    @Test
    public void testNotFoundInCache() {

        assertNull(runResults.startRun(this.getClass(), "eg1"));
        assertNull(runResults.startRun(this.getClass(), "eg2"));
        assertNull(runResults.startRun(JustAnotherClass.class, "eg3"));

    }

}
