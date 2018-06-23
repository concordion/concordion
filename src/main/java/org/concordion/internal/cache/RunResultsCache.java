package org.concordion.internal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureType;
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
     * @param fixtureClass class to retrieve from the cache
     * @param example the example to get from the cache
     * @return can return null if not in the cache
     */
    public synchronized RunOutput getFromCache(Class<?> fixtureClass, String example) {
        return map.get(getID(fixtureClass, example));
    }

    /**
     * Provides a direct method to access the cache
     *
     * @param fixtureType class to retrieve from the cache
     * @param example the example to get from the cache
     * @return can return null if not in the cache
     */
    public synchronized RunOutput getFromCache(FixtureType fixtureType, String example) {
        return map.get(getID(fixtureType, example));
    }

    /**
     * Initialises an entry in the cache for a specification with the given description.
     * Individual examples may then be run, and their results will be added to the fixture
     * total when {@link #finishRun(FixtureDeclarations, String, ResultSummary, ImplementationStatusChecker)}
     * is called.
     * @param fixtureType the fixture class that is being started
     * @param specificationDescription a description of the target specification
     */
    public synchronized void startFixtureRun(FixtureType fixtureType, String specificationDescription) {
        CompositeRunOutput fixtureRunOutput = new CompositeRunOutput(specificationDescription);
        map.put(getID(fixtureType, null), fixtureRunOutput);
    }

    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues.
     *
     * @param fixtureType the fixture class to retrieve
     * @param example the name of the example that is being started (null OK)
     * @return the result summary from the cache
     */
    public synchronized RunOutput startRun(FixtureType fixtureType, String example) {
        assert fixtureType.getFixtureClass() != null;

        // check to see if there is a result in the cache
        RunOutput runSummary = getExampleFromCache(fixtureType, example);
        if (runSummary != null) {
            return runSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new ConcordionRunOutput();
        map.put(getID(fixtureType, example), runSummary);
        return null;
    }

    private CacheKey getID(FixtureType fixtureType, String example) {
        return getID(fixtureType.getFixtureClass(), example);
    }

    private CacheKey getID(Class<? extends Object> fixtureClass, String example) {
        return new CacheKey(fixtureClass, example);
    }

    /**
     * Updates the cache with the results of a run.
     * @param fixtureType the fixture class to update
     * @param example the name of the example that is being finished (null ok)
     * @param actualResultSummary the results as reported from the spec
     * @param statusChecker modifier that updates results dependent on ImplementationStatus (ExpectedToFail etc)
     */
    public synchronized void finishRun(FixtureType fixtureType, String example,
                                       ResultSummary actualResultSummary,
                                       ImplementationStatusChecker statusChecker) {
        assert fixtureType.getFixtureClass() != null;
        assert actualResultSummary != null;

        RunOutput exampleRunOutput = setResultsForExample(fixtureType, example, actualResultSummary, statusChecker);

        if (example != null) {
            addResultsToFixtureTotal(fixtureType, exampleRunOutput);
        }
    }

    private RunOutput setResultsForExample(FixtureType fixtureType, String example, ResultSummary actualResultSummary, ImplementationStatusChecker statusChecker) {
        ConcordionRunOutput exampleResults = (ConcordionRunOutput) getExampleFromCache(fixtureType, example);
        if (exampleResults == null) {
            throw new IllegalStateException("Internal error: startRun must always be called before finishRun");
        }
        exampleResults.setStatusChecker(statusChecker);
        exampleResults.setActualResultSummary(actualResultSummary);
        return exampleResults;
    }

    private void addResultsToFixtureTotal(FixtureType fixtureType, RunOutput exampleRunOutput) {
        CompositeRunOutput fixtureRunOutput = (CompositeRunOutput) getFromCache(fixtureType.getFixtureClass(), null);
        fixtureRunOutput.add(exampleRunOutput);
    }

    private RunOutput getExampleFromCache(FixtureType fixtureType, String example) {
        return map.get(getID(fixtureType, example));
    }

    /**
     * Run failed. Remove our placeholder value.
     * @param fixtureType fixture class that failed
     * @param example the name of the example that failed
     */
    public synchronized void failRun(FixtureType fixtureType, String example) {
        map.remove(getID(fixtureType, example));
    }

    /**
     * For test purposes only
     * @param fixtureType fixture class to remove cache results for
     */
    public void removeAllFromCache(FixtureType fixtureType) {
        for (CacheKey key : map.keySet()) {
            if (key.isForClass(fixtureType.getFixtureClass())) {
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
