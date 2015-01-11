package test.concordion.internal.runner;

import org.concordion.internal.CachedRunResults;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by TimW5 on 27/09/14.
 *
 * @author TimW5
 */
public class CachedResultsUnitTest {

    @Test
    public void testCacheInProgress() {
        CachedRunResults runResults = new CachedRunResults();

        assertNull(runResults.startRun(this.getClass(), null));
        assertNotNull(runResults.startRun(this.getClass(), null));

    }

    @Test
    public void testCacheInProgressWithExample() {
        CachedRunResults runResults = new CachedRunResults();

        assertNull(runResults.startRun(this.getClass(), "eg"));
        assertNotNull(runResults.startRun(this.getClass(), "eg"));

    }

    private static class JustAnotherClass {};

    @Test
    public void testNotFoundInCache() {
        CachedRunResults runResults = new CachedRunResults();

        assertNull(runResults.startRun(this.getClass(), "eg1"));
        assertNull(runResults.startRun(this.getClass(), "eg2"));
        assertNull(runResults.startRun(JustAnotherClass.class, "eg3"));

    }

}
