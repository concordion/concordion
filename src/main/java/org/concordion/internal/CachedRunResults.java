package org.concordion.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

/**
 *
 * A thread-safe class to provide caching of run results.
 *
 * @author Tim Wright <tim@tfwright.co.nz>
 *
 */
public class CachedRunResults {

    /**
     * Basic status of a cache entry
     */
    private enum RunStatus {
        NOT_STARTED,
        RUNNING,
        FINISHED
    }

    /**
     * Data to store in the cache
     */
    private class CachedRunSummary {
        private RunStatus status;
        private ResultSummary resultSummary;
        public CachedRunSummary(RunStatus runStatus, Class<?> fixtureClass) {
            status = runStatus;
            SingleResultSummary singleResultSummary = new SingleResultSummary(Result.IGNORED);
//            singleResultSummary.setSpecificationDescription("In progress result summary for " + fixtureClass.getName());
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

        // no cached result, so update the cache with a "running" state - that means we can detect circular dependancies
        runSummary = new CachedRunSummary(RunStatus.RUNNING, fixtureClass);
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
            runSummary = new CachedRunSummary(RunStatus.FINISHED, fixtureClass);
        }

        // update the cache
        runSummary.status = RunStatus.FINISHED;
        runSummary.resultSummary = resultSummary;

        map.put(fixtureClass, runSummary);
	}

}
