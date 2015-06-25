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
        CachedRunResults runResults = CachedRunResults.SINGLETON;
        runResults.clearCache();

        assertNull(runResults.startRun(this.getClass()));
        assertNotNull(runResults.startRun(this.getClass()));

    }

}
