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

    private static class CacheKey {
        final String example;
        final Class<?> clas;

        CacheKey(Class<?> clas, String example) {
            assert clas != null;
            this.clas = clas; this.example = example;
        }
        @Override
        public int hashCode() {
            int classHash = clas.hashCode();
            int exampleHash = example==null?0:example.hashCode();

            // use 11 because it's prime
            return classHash * 11 + exampleHash;
        }
        @Override
        public boolean equals(Object o) {

            if(!(o instanceof CacheKey)) {
                return super.equals(o);
            }

            CacheKey other = (CacheKey) o;

            boolean classesEqual = clas.equals(other.clas);
            boolean examplesEqual = example==null?other.example==null :example.equals(other.example);

            return classesEqual && examplesEqual;
        }
    }

	Map<CacheKey, CachedRunSummary> map = new ConcurrentHashMap<CacheKey, CachedRunSummary>();

    /**
     * Searches for a match in the cache. If there is no match, it marks the test as "in progress".
     * This is done in one method to avoid thread synchronization issues
     *
     * @param fixtureClass the class to retrieve
     * @param example the name of the example that is being started (null OK)
     * @return the result summary from the cache
     */
    public synchronized ResultSummary startRun(Class<? extends Object> fixtureClass, String example) {
        assert fixtureClass != null;

        // check to see if there is a result in the cache
        CachedRunSummary runSummary = map.get(getID(fixtureClass, example));
        if (runSummary != null) {
            return runSummary.resultSummary;
        }

        // no cached result, so update the cache - that means we can detect circular dependencies
        runSummary = new CachedRunSummary(fixtureClass);
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
     * @param resultSummary the results
     */
    public synchronized void finishRun(Class<?> fixtureClass, String example, ResultSummary resultSummary) {
        assert fixtureClass != null;
        assert resultSummary != null;

        // check if there is already a result
        CachedRunSummary runSummary = map.get(getID(fixtureClass, example));
        if (runSummary == null) {
            // no result? Create one. This should never happen because startRun should always be called before
            // finishRun
            runSummary = new CachedRunSummary(fixtureClass);
        }

        // update the cache
        runSummary.resultSummary = resultSummary;
        map.put(getID(fixtureClass, example), runSummary);
	}

}
