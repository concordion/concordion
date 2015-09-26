package org.concordion.internal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.ResultSummary;
import org.concordion.internal.Fixture;
import org.concordion.internal.SingleResultSummary;
import org.concordion.internal.SummarizingResultRecorder;

/**
 *
 * A thread-safe class to provide caching of run results.
 *
 * @author Tim Wright &lt;tim@tfwright.co.nz&gt;
 *
 */
public enum RunResultsCache {
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
     * @param fixtureClass class to retrieve from the cache
     * @param example the example to get from the cache
     * @return can return null if not in the cache
     */
    public synchronized  ConcordionRunOutput getFromCache(Class<?> fixtureClass, String example) {
        ConcordionRunOutput summary = map.get(getID(fixtureClass, example));

        // check for nothing in cache
        if (summary == null) {
            return null;
        }

        return summary;
    }


    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues
     * @param fixture the fixture to retrieve
     * @param example the name of the example that is being started (null OK)
     *
     * @return the result summary from the cache
     */
    public synchronized ConcordionRunOutput startRun(Fixture fixture, String example) {
        assert fixture != null;

        // check to see if there is a result in the cache
        ConcordionRunOutput runSummary = map.get(getID(fixture, example));
        if (runSummary != null) {
            return runSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new ConcordionRunOutput(fixture);
        map.put(getID(fixture, example), runSummary);
        return null;
    }

    private CacheKey getID(Fixture fixture, String example) {
        return getID(fixture.getFixtureClass(), example);
    }

    private CacheKey getID(Class<? extends Object> fixtureClass, String example) {
        return new CacheKey(fixtureClass, example);
    }

    /**
     * Updates the cache with the results of a run
     *
     * @param fixture the class to update
     * @param example the name of the example that is being finished (null ok)
     * @param actualSummary the results as reported from the spec
     * @param postProcessedResultSummary the results as post processed by any fixture annotations
     */
    public synchronized void finishRun(Fixture fixture,
                                       String example,
                                       ResultSummary actualSummary,
                                       ResultSummary postProcessedResultSummary) {
        assert fixture != null;
        assert actualSummary != null;
        assert postProcessedResultSummary != null;

        // check if there is already a result
        ConcordionRunOutput runSummary = map.get(getID(fixture, example));
        if (runSummary == null) {
            // no result? Create one. This should never happen because startRun should always be called before
            // finishRun
            runSummary = new ConcordionRunOutput(fixture);
            map.put(getID(fixture, example), runSummary);
        }

        // update the cached value
        runSummary.setActualResultSummary(actualSummary);
        runSummary.setModifiedResultSummary(postProcessedResultSummary);

        String specificationDescription = fixture.getDefaultFixtureClassName();


        // now accumulate into the parent
        ConcordionRunOutput output = map.get(getID(fixture, null));
        if (output == null) {
            output = new ConcordionRunOutput(fixture);
            map.put(getID(fixture, null), output);
            // give them empty result summaries by default
            output.setActualResultSummary(new SummarizingResultRecorder(specificationDescription));
            output.setModifiedResultSummary(new SummarizingResultRecorder(specificationDescription));
        }

        // and now accumulate
        SummarizingResultRecorder totalActualResults = new SummarizingResultRecorder(specificationDescription);
        totalActualResults.record(output.getActualResultSummary());
        if (actualSummary.isForExample()) {
            totalActualResults.record(new SingleResultSummary(actualSummary));
        } else {
            totalActualResults.record(actualSummary);
        }
        output.setActualResultSummary(totalActualResults);

        SummarizingResultRecorder totalConvertedResults = new SummarizingResultRecorder(specificationDescription);
        totalConvertedResults.record(output.getModifiedResultSummary());
        if (postProcessedResultSummary.isForExample()) {
            totalConvertedResults.record(new SingleResultSummary(postProcessedResultSummary));
        } else {
            totalConvertedResults.record(postProcessedResultSummary);
        }
        output.setModifiedResultSummary(totalConvertedResults);

    }

    public void failRun(Fixture fixture, String example) {
        // run failed. Remove our placeholder value.
        map.remove(getID(fixture, example));
    }

    public void removeAllFromCache(Fixture fixture) {
        for (CacheKey key : map.keySet()) {
            if (key.isForClass(fixture.getFixtureClass())) {
                map.remove(key);
            }
        }
    }
}

