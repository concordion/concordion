package test.concordion.internal.runner;

import org.concordion.internal.cache.CachedRunResults;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by TimW5 on 27/09/14.
 *
 * @author TimW5
 */
public class CachedResultsUnitTest {

    public static final String EXAMPLE_1_NAME = "eg1";
    public static final String EXAMPLE_2_NAME = "eg2";
    
    private CachedRunResults runResults = CachedRunResults.SINGLETON;

    @Before
    public void before() {
        runResults.removeAllFromCache(this.getClass());
    }

    @Test
    public void testCacheInProgress() {

        // basically before a run, runResults.startRun should return null - showing that nothing is in progress
        assertNull(runResults.startRun(this.getClass(), null));

        // but if it's called again, we'll get some "in progress" results
        ConcordionRunOutput concordionRunOutput = runResults.startRun(this.getClass(), null);
        assertNotNull(concordionRunOutput);
        assertThat(concordionRunOutput.getActualResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getIgnoredCount(), is(equalTo(1L)));

        assertThat(concordionRunOutput.getPostProcessedResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getPostProcessedResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getPostProcessedResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getPostProcessedResultSummary().getIgnoredCount(), is(equalTo(1L)));

    }

    @Test
    public void testCacheInProgressWithExample() {

        // basically before a run, runResults.startRun should return null - showing that nothing is in progress
        assertNull(runResults.startRun(this.getClass(), EXAMPLE_1_NAME));

        // but if it's called again, we'll get some "in progress" results
        ConcordionRunOutput concordionRunOutput = runResults.startRun(this.getClass(), EXAMPLE_1_NAME);
        assertNotNull(concordionRunOutput);
        assertThat(concordionRunOutput.getActualResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getActualResultSummary().getIgnoredCount(), is(equalTo(1L)));

        assertThat(concordionRunOutput.getPostProcessedResultSummary().getExceptionCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getPostProcessedResultSummary().getSuccessCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getPostProcessedResultSummary().getFailureCount(), is(equalTo(0L)));
        assertThat(concordionRunOutput.getPostProcessedResultSummary().getIgnoredCount(), is(equalTo(1L)));

    }


    @Test
    public void testNotFoundInCache() {

        // some tests to check that examples are independent

        // two examples from the same class
        assertNull(runResults.startRun(this.getClass(), EXAMPLE_1_NAME));
        assertNull(runResults.startRun(this.getClass(), EXAMPLE_2_NAME));

        // now a same named example from a different class
        assertNull(runResults.startRun(JustAnotherClass.class, EXAMPLE_2_NAME));
    }

    private static class JustAnotherClass {};

}
