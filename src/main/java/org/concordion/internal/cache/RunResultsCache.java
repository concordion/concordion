package org.concordion.internal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.Fixture;
import org.concordion.api.ResultSummary;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.XMLSpecification;

/**
 * A thread-safe class to provide caching of run results.
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
    * Provides a direct method to access the cache
    *
    * @param fixture fixture containing class to retrieve from the cache
    * @param example the example to get from the cache
    * @return can return null if not in the cache
    */
    public ConcordionRunOutput getFromCache(Fixture fixture, String example) {
        return getFromCache(fixture.getFixtureClass(), example);
    }
    
    /**
     * Provides a direct method to access the cache
     *
     * @param fixtureClass class to retrieve from the cache
     * @param example the example to get from the cache
     * @return can return null if not in the cache
     */
    public synchronized ConcordionRunOutput getFromCache(Class<?> fixtureClass, String example) {
        return map.get(getID(fixtureClass, example));
    }


    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues.
     * 
     * @param fixture the fixture to retrieve
     * @param example the name of the example that is being started (null OK)
     *
     * @return the result summary from the cache
     */
    public synchronized ConcordionRunOutput startRun(Fixture fixture, String example) {
        assert fixture.getFixtureClass() != null;

        // check to see if there is a result in the cache
        ConcordionRunOutput runSummary = getExampleFromCache(fixture, example);
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
     * Updates the cache with the results of a run.
     * @param fixture the fixture to update
     * @param example the name of the example that is being finished (null ok)
     * @param actualResultSummary the results as reported from the spec
     * @param modifiedResultSummary the results as post processed by any fixture annotations
     */
    public synchronized void finishRun(Fixture fixture,
                                       String example,
                                       ResultSummary actualResultSummary,
                                       ResultSummary modifiedResultSummary) {
        assert fixture.getFixtureClass() != null;
        assert actualResultSummary != null;
        assert modifiedResultSummary != null;

        setResultsForExample(fixture, example, actualResultSummary, modifiedResultSummary);

        addResultsToFixtureTotal(fixture, actualResultSummary, modifiedResultSummary);
    }

    private void setResultsForExample(Fixture fixture, String example, ResultSummary actualResultSummary, ResultSummary modifiedResultSummary) {
        ConcordionRunOutput exampleResults = getExampleFromCache(fixture, example);
        if (exampleResults == null) {
            throw new IllegalStateException("Internal error: startRun must always be called before finishRun");
        }
        if (isOuterExample(example)) {
            // Clone since the outer example is also used for the total result summary
            actualResultSummary = clone(actualResultSummary);
            modifiedResultSummary = clone(modifiedResultSummary);
        }
        exampleResults.setActualResultSummary(actualResultSummary);
        exampleResults.setModifiedResultSummary(modifiedResultSummary);
    }

    private boolean isOuterExample(String example) {
        return XMLSpecification.OUTER_EXAMPLE_NAME.equals(example);
    }

    private void addResultsToFixtureTotal(Fixture fixture, ResultSummary actualResultSummary, ResultSummary modifiedResultSummary) {
        ConcordionRunOutput fixtureTotalResults = map.get(getID(fixture, null));
        if (fixtureTotalResults == null) {
            if (actualResultSummary == modifiedResultSummary) {
                // Clone so that we don't add the results twice to the same summary
                modifiedResultSummary = clone(modifiedResultSummary);  
            }
            fixtureTotalResults = new ConcordionRunOutput(actualResultSummary, modifiedResultSummary);
            map.put(getID(fixture, null), fixtureTotalResults);
        } else {
            ResultSummary totalActualResults = addResults(fixtureTotalResults.getActualResultSummary(), actualResultSummary);
            fixtureTotalResults.setActualResultSummary(totalActualResults);
            ResultSummary totalModifiedResults = addResults(fixtureTotalResults.getModifiedResultSummary(), modifiedResultSummary);
            fixtureTotalResults.setModifiedResultSummary(totalModifiedResults);                  }
    }

    private ResultSummary clone(ResultSummary resultSummary) {
        SummarizingResultRecorder clone = new SummarizingResultRecorder(resultSummary.getSpecificationDescription());
        clone.record(resultSummary);
        return clone;
    }


    private ResultSummary addResults(ResultSummary accumulator, ResultSummary resultsToAdd) {
        SummarizingResultRecorder recorder;
        if (accumulator instanceof SummarizingResultRecorder) {
            recorder = (SummarizingResultRecorder) accumulator;
        } else {
            recorder = new SummarizingResultRecorder();
            recorder.record(accumulator);
        }
        recorder.record(resultsToAdd);
        return recorder;
    }

    private ConcordionRunOutput getExampleFromCache(Fixture fixture, String example) {
        return map.get(getID(fixture, example));
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
    
    private static class CacheKey {
        final String example;
        final Class<?> clas;

        public CacheKey(Class<?> clas, String example) {
            assert clas != null;
            this.clas = clas;
            this.example = example;
        }

        @Override
        public int hashCode() {
            int classHash = clas.hashCode();
            int exampleHash = example == null ? 0 : example.hashCode();

            // use 11 because it's prime
            return classHash * 11 + exampleHash;
        }

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof CacheKey)) {
                return super.equals(o);
            }

            CacheKey other = (CacheKey) o;

            boolean classesEqual = clas.equals(other.clas);
            boolean examplesEqual = example == null ? other.example == null : example.equals(other.example);

            return classesEqual && examplesEqual;
        }

        public boolean isForClass(Class<?> aClass) {
            return this.clas.equals(aClass);
        }
    }
}
