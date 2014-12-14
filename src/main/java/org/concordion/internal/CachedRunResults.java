package org.concordion.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

/**
 *
 * A thread-safe class to provide caching of run results.
 *
 * @author Tim Wright &lt;tim@tfwright.co.nz&gt;
 *
 */
public class CachedRunResults {

    /**
     * Data to store in the cache
     */
    private static class CachedRunSummary {
        private ResultSummary resultSummary;
        public CachedRunSummary(Class<?> fixtureClass) {
            SingleResultSummary singleResultSummary = new SingleResultSummary(Result.IGNORED);
            singleResultSummary.setSpecificationDescription("In progress result summary for " + fixtureClass.getName());
            this.resultSummary = singleResultSummary;
        }
    }

	Map<Class<?>, CachedRunSummary> map = new ConcurrentHashMap<Class<?>, CachedRunSummary>();

    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues
     *
     * @param fixtureClass the class to retrieve
     * @return the result summary from the cache
     */
    public synchronized ResultSummary startRun(Class<? extends Object> fixtureClass) {

        // check to see if there is a result in the cache
        CachedRunSummary runSummary = map.get(fixtureClass);
        if (runSummary != null) {
            return runSummary.resultSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new CachedRunSummary(fixtureClass);
        map.put(fixtureClass, runSummary);
        return null;
    }

    /**
     * Updates the cache with the results of a run
     *
     * @param fixtureClass the class to update
     * @param resultSummary the results
     */
    public synchronized void finishRun(Class<?> fixtureClass, ResultSummary resultSummary) {
        // check if there is already a result
        CachedRunSummary runSummary = map.get(fixtureClass);
        if (runSummary == null) {
            // no result? Create one. This should never happen because startRun should always be called before
            // finishRun
            runSummary = new CachedRunSummary(fixtureClass);
        }

        // update the cache
        runSummary.resultSummary = resultSummary;
        map.put(fixtureClass, runSummary);
	}

}
