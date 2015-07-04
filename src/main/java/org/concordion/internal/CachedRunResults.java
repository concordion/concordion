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

	Map<Class<?>, ConcordionRunOutput> map = new ConcurrentHashMap<Class<?>, ConcordionRunOutput>();

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


    public synchronized  ConcordionRunOutput getFromCache(Class<?> testClass) {
        ConcordionRunOutput summary = map.get(testClass);

        // check for nothing in cache
        if (summary == null) {
            return null;
        }

        return summary;
    }

//    /**
//     *
//     * Provides a direct method to put a result summary in the class. Generally should be used if a test is being
//     * invoked but it's not a Concordion test (ie: concorion:run is being called on a regular junit test)
//     *
//     * @param testClass the class to enter into the cache
//     * @param summary the summary to enter into the cache
//     */
//    public synchronized void enterIntoCache(Class<?> testClass, ResultSummary summary) {
//
//        ConcordionRunOutput runSummary = map.get(testClass);
//
//        // check for nothing in cache
//        if (runSummary == null) {
//            runSummary = new ConcordionRunOutput(testClass);
//            map.put(testClass, runSummary);
//        }
//
//        runSummary.setActualResultSummary(summary);
//    }


    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues
     *
     * @param fixtureClass the class to retrieve
     * @return the result summary from the cache
     */
    public synchronized ConcordionRunOutput startRun(Class<? extends Object> fixtureClass) {

        // check to see if there is a result in the cache
        ConcordionRunOutput runSummary = map.get(fixtureClass);
        if (runSummary != null) {
            return runSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new ConcordionRunOutput(fixtureClass);
        map.put(fixtureClass, runSummary);
        return null;
    }

    /**
     * Updates the cache with the results of a run
     *  @param fixtureClass the class to update
     * @param actualSummary the actual summary returned
     * @param convertedSummary the summary converted by any annotations
     */
    public synchronized void finishRun(Class<?> fixtureClass,
                                       ResultSummary actualSummary,
                                       ResultSummary convertedSummary) {
        // check if there is already a result
        ConcordionRunOutput runSummary = map.get(fixtureClass);
        if (runSummary == null) {
            // no result? Create one. This should never happen because startRun should always be called before
            // finishRun
            runSummary = new ConcordionRunOutput(fixtureClass);
        }

        // update the cache
        runSummary.setActualResultSummary(actualSummary);
        runSummary.setPostProcessedResultSummary(convertedSummary);
        map.put(fixtureClass, runSummary);
	}

    public ResultSummary convertForCache(ResultSummary rs, Class<?> fixtureClass) {
        FixtureState state = FixtureState.getFixtureState(fixtureClass);
        return state.convertForCache(rs);
    }

    public void failRun(Class<? extends Object> aClass) {
        // run failed. Remove our placeholder value.
        map.remove(aClass);
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