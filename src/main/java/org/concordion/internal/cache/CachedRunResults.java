package org.concordion.internal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.Concordion;
import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureState;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.cache.CacheKey;
import org.concordion.internal.cache.ConcordionRunOutput;

/**
 *
 * A thread-safe class to provide caching of run results.
 *
 * @author Tim Wright &lt;tim@tfwright.co.nz&gt;
 *
 */
public enum CachedRunResults {
    SINGLETON;

	Map<CacheKey, ConcordionRunOutput> map = new ConcurrentHashMap<CacheKey, ConcordionRunOutput>();

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
     * @param example the example to get from the cache
     * @return can return null if not in the cache
     */
    public synchronized  ConcordionRunOutput getFromCache(Class<?> testClass, String example) {
        ConcordionRunOutput summary = map.get(getID(testClass, example));

        // check for nothing in cache
        if (summary == null) {
            return null;
        }

        return summary;
    }


    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues
     *
     * @param fixtureClass the class to retrieve
     * @param example the name of the example that is being started (null OK)
     * @return the result summary from the cache
     */
    public synchronized ConcordionRunOutput startRun(Class<? extends Object> fixtureClass, String example) {
        assert fixtureClass != null;


        // check to see if there is a result in the cache
        ConcordionRunOutput runSummary = map.get(getID(fixtureClass, example));
        if (runSummary != null) {
            return runSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new ConcordionRunOutput(fixtureClass);
        map.put(getID(fixtureClass, example), runSummary);
        return null;
    }

    private CacheKey getID(Class<? extends Object> fixtureClass, String example) {
        return new CacheKey(fixtureClass, example);
    }

    /**
     * Updates the cache with the results of a run
     *
     * @param fixtureClass the class to update
     * @param example the name of the example that is being finished (null ok)
     * @param actualSummary the results as reported from the spec
     * @param convertedSummary the results as post processed by any fixture annotations
     */
    public synchronized void finishRun(Class<?> fixtureClass,
                                       String example,
                                       ResultSummary actualSummary,
                                       ResultSummary convertedSummary) {
        assert fixtureClass != null;
        assert actualSummary != null;
        assert convertedSummary != null;

        // check if there is already a result
        ConcordionRunOutput runSummary = map.get(getID(fixtureClass, example));
        if (runSummary == null) {
            // no result? Create one. This should never happen because startRun should always be called before
            // finishRun
            runSummary = new ConcordionRunOutput(fixtureClass);
            map.put(getID(fixtureClass, example), runSummary);
        }

        // update the cached value
        runSummary.setActualResultSummary(actualSummary);
        runSummary.setPostProcessedResultSummary(convertedSummary);

        String specificationDescription = Concordion.getDefaultFixtureClassName(fixtureClass);


        // now accumulate into the parent
        ConcordionRunOutput output = map.get(getID(fixtureClass, null));
        if (output == null) {
            output = new ConcordionRunOutput(fixtureClass);
            map.put(getID(fixtureClass, null), output);
            // give them empty result summaries by default
            output.setActualResultSummary(new SummarizingResultRecorder(specificationDescription));
            output.setPostProcessedResultSummary(new SummarizingResultRecorder(specificationDescription));
        }

        // and now accumulate
        SummarizingResultRecorder totalActualResults = new SummarizingResultRecorder(specificationDescription);
        totalActualResults.record(output.getActualResultSummary());
        totalActualResults.record(actualSummary);
        output.setActualResultSummary(totalActualResults);

        SummarizingResultRecorder totalConvertedResults = new SummarizingResultRecorder(specificationDescription);
        totalConvertedResults.record(output.getPostProcessedResultSummary());
        totalConvertedResults.record(convertedSummary);
        output.setPostProcessedResultSummary(totalConvertedResults);

    }

    public ResultSummary convertForCache(ResultSummary rs, Class<?> fixtureClass) {
        FixtureState state = FixtureState.getFixtureState(fixtureClass);
        return state.convertForCache(rs);
    }

    public void failRun(Class<? extends Object> aClass, String example) {
        // run failed. Remove our placeholder value.
        map.remove(getID(aClass, example));
    }
}

