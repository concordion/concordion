package org.concordion.internal;

import org.concordion.api.ResultSummary;

/**
 * Returns the results of running a test. Applies to multiple levels, eg. example or specification level.
 */
public interface RunOutput {
    /**
     * The actual results of the test run.
     * @return actual results
     */
    ResultSummary getActualResultSummary();

    /**
     * The results of the test run, modified to account for {@link org.concordion.api.ImplementationStatus}.
     * @return modified results
     */
    ResultSummary getModifiedResultSummary();
}
