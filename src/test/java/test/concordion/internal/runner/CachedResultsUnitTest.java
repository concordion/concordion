package test.concordion.internal.runner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.concordion.internal.FixtureInstance;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.concordion.internal.cache.RunResultsCache;
import org.junit.Before;
import org.junit.Test;

public class CachedResultsUnitTest {

    public static final String EXAMPLE_1_NAME = "eg1";
    public static final String EXAMPLE_2_NAME = "eg2";
    
    private RunResultsCache runResults = RunResultsCache.SINGLETON;

    @Before
    public void before() {
        runResults.removeAllFromCache(new FixtureInstance(this));
    }

    @Test
    public void testCacheInProgress() {

        // basically before a run, runResults.startRun should return null - showing that nothing is in progress
        assertNull(runResults.startRun(new FixtureInstance(this), null));

        // but if it's called again, we'll get some "in progress" results
        ConcordionRunOutput concordionRunOutput = runResults.startRun(new FixtureInstance(this), null);
        assertNotNull(concordionRunOutput);
        assertThat(concordionRunOutput.getActualResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getIgnoredCount(), is(equalTo(1L)));

        assertThat(concordionRunOutput.getModifiedResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getModifiedResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getModifiedResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getModifiedResultSummary().getIgnoredCount(), is(equalTo(1L)));

    }

    @Test
    public void testCacheInProgressWithExample() {

        // basically before a run, runResults.startRun should return null - showing that nothing is in progress
        assertNull(runResults.startRun(new FixtureInstance(this), EXAMPLE_1_NAME));

        // but if it's called again, we'll get some "in progress" results
        ConcordionRunOutput concordionRunOutput = runResults.startRun(new FixtureInstance(this), EXAMPLE_1_NAME);
        assertNotNull(concordionRunOutput);
        assertThat(concordionRunOutput.getActualResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getIgnoredCount(), is(equalTo(1L)));

        assertThat(concordionRunOutput.getModifiedResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getModifiedResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getModifiedResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getModifiedResultSummary().getIgnoredCount(), is(equalTo(1L)));

    }


    @Test
    public void testNotFoundInCache() {

        // some tests to check that examples are independent

        // two examples from the same class
        assertNull(runResults.startRun(new FixtureInstance(this), EXAMPLE_1_NAME));
        assertNull(runResults.startRun(new FixtureInstance(this), EXAMPLE_2_NAME));

        // now a same named example from a different class
        assertNull(runResults.startRun(new FixtureInstance(new JustAnotherClass()), EXAMPLE_2_NAME));
    }

    private static class JustAnotherClass {};

}
