package org.concordion.internal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.Fixture;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ImplementationStatusChecker;
import org.concordion.internal.RunOutput;

/**
 * A thread-safe class to provide caching of run results.
 */
public enum RunResultsCache {
    SINGLETON;

	Map<CacheKey, RunOutput> map = new ConcurrentHashMap<CacheKey, RunOutput>();

    /**
    * Provides a direct method to access the cache
    *
    * @param fixture fixture containing class to retrieve from the cache
    * @param example the example to get from the cache
    * @return can return null if not in the cache
    */
    public RunOutput getFromCache(Fixture fixture, String example) {
        return getFromCache(fixture.getFixtureClass(), example);
    }

    /**
     * Provides a direct method to access the cache
     *
     * @param fixtureClass class to retrieve from the cache
     * @param example the example to get from the cache
     * @return can return null if not in the cache
     */
    public synchronized RunOutput getFromCache(Class<?> fixtureClass, String example) {
        return map.get(getID(fixtureClass, example));
    }

    /**
     * Initialises an entry in the cache for a specification with the given description.
     * Individual examples may then be run, and their results will be added to the fixture
     * total when {@link #finishRun(Fixture, String, ResultSummary, ImplementationStatusChecker)}
     * is called.
     *
     * @param fixture the fixture that is being started
     * @param specificationDescription a description of the target specification
     */
    public synchronized void startFixtureRun(Fixture fixture, String specificationDescription) {
        CompositeRunOutput fixtureRunOutput = new CompositeRunOutput(specificationDescription);
        map.put(getID(fixture, null), fixtureRunOutput);
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
    public synchronized RunOutput startRun(Fixture fixture, String example) {
        assert fixture.getFixtureClass() != null;

        // check to see if there is a result in the cache
        RunOutput runSummary = getExampleFromCache(fixture, example);
        if (runSummary != null) {
            return runSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new ConcordionRunOutput();
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
     * @param statusChecker modifier that updates results dependent on ImplementationStatus (ExpectedToFail etc)
     */
    public synchronized void finishRun(Fixture fixture,
                                       String example,
                                       ResultSummary actualResultSummary,
                                       ImplementationStatusChecker statusChecker) {
        assert fixture.getFixtureClass() != null;
        assert actualResultSummary != null;

        RunOutput exampleRunOutput = setResultsForExample(fixture, example, actualResultSummary, statusChecker);

        if (example != null) {
            addResultsToFixtureTotal(fixture, exampleRunOutput);
        }
    }

    private RunOutput setResultsForExample(Fixture fixture, String example, ResultSummary actualResultSummary, ImplementationStatusChecker statusChecker) {
        ConcordionRunOutput exampleResults = (ConcordionRunOutput) getExampleFromCache(fixture, example);
        if (exampleResults == null) {
            throw new IllegalStateException("Internal error: startRun must always be called before finishRun");
        }
        exampleResults.setStatusChecker(statusChecker);
        exampleResults.setActualResultSummary(actualResultSummary);
        return exampleResults;
    }

    private void addResultsToFixtureTotal(Fixture fixture, RunOutput exampleRunOutput) {
        CompositeRunOutput fixtureRunOutput = (CompositeRunOutput) getFromCache(fixture.getFixtureClass(), null);
        fixtureRunOutput.add(exampleRunOutput);
    }

    private RunOutput getExampleFromCache(Fixture fixture, String example) {
        return map.get(getID(fixture, example));
    }

    /**
     * Run failed. Remove our placeholder value.
     * @param fixture the fixture to update
     * @param example the name of the example that failed
     */
    public synchronized void failRun(Fixture fixture, String example) {
        map.remove(getID(fixture, example));
    }

    /**
     * For test purposes only
     * @param fixture fixture to remove cache results for
     */
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
