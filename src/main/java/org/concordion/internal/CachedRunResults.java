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
public enum CachedRunResults {
    SINGLETON;

	Map<Class<?>, CachedRunSummary> map = new ConcurrentHashMap<Class<?>, CachedRunSummary>();

    /**
     *
     * Clears the cache. Should only really be used by tests.
     *
     */
    public synchronized  void clearCache() {
        map.clear();
    }

    /**
     *
     * Provides a direct method to access the cache
     *
     * @param testClass class to retrieve from the cache
     * @return can return null if not in the cache
     */


    public synchronized  ResultSummary getFromCache(Class<?> testClass) {
        CachedRunSummary summary = map.get(testClass);

        // check for nothing in cache
        if (summary == null) {
            return null;
        }

        return summary.getResultSummary();
    }

    /**
     *
     * Provides a direct method to put a result summary in the class. Generally should be used if a test is being
     * invoked but it's not a Concordion test (ie: concorion:run is being called on a regular junit test)
     *
     * @param testClass the class to enter into the cache
     * @param summary the summary to enter into the cache
     */
    public synchronized void enterIntoCache(Class<?> testClass, ResultSummary summary) {

        CachedRunSummary runSummary = map.get(testClass);

        // check for nothing in cache
        if (runSummary == null) {
            runSummary = new CachedRunSummary(testClass);
            map.put(testClass, runSummary);
        }

        runSummary.setResultSummary(summary);
    }


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
            return runSummary.getResultSummary();
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
        runSummary.setResultSummary(resultSummary);
        map.put(fixtureClass, runSummary);
	}

    public ResultSummary convertForCache(ResultSummary rs, Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture);
        return state.convertForCache(rs);
    }
}

/**
 * Data to store in the cache
 */
class CachedRunSummary {
    private ResultSummary resultSummary;
    public CachedRunSummary(Class<?> fixtureClass) {
        SingleResultSummary singleResultSummary = new CacheResultSummary(Result.IGNORED,
                "No current results for fixture " + fixtureClass.getName() + " as the specification is currently being executed");
        this.resultSummary = singleResultSummary;
    }

    public ResultSummary getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(ResultSummary resultSummary) {
        this.resultSummary = resultSummary;
    }
}

class CacheResultSummary extends SingleResultSummary {

    public CacheResultSummary(Result result, String specificationDescription) {
        super(result, specificationDescription);
    }

    @Override
    public String printCountsToString(Object fixture) {
        // no counts for cached result summary
        return null;
    }
}