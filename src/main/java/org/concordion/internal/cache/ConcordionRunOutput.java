package org.concordion.internal.cache;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ImplementationStatusChecker;
import org.concordion.internal.RunOutput;

/**
 * Data to store in the cache
 */
class ConcordionRunOutput implements RunOutput {
    private static final CacheResultSummary IN_PROGRESS_RESULT_SUMMARY = new CacheResultSummary(Result.IGNORED,
            "No current results as the specification is currently being executed");
    private ResultSummary actualResultSummary;
    private ImplementationStatusChecker statusChecker;

    public ConcordionRunOutput() {
        this.actualResultSummary = IN_PROGRESS_RESULT_SUMMARY;
        statusChecker = ImplementationStatusChecker.EXPECTED_TO_PASS;
    }

    @Override
    public ResultSummary getActualResultSummary() {
        return actualResultSummary;
    }

    public void setActualResultSummary(ResultSummary actualResultSummary) {
        this.actualResultSummary = actualResultSummary;
    }

    @Override
    public ResultSummary getModifiedResultSummary() {
        return statusChecker.convertForCache(getActualResultSummary());
    }

    public void setStatusChecker(ImplementationStatusChecker statusChecker) {
        this.statusChecker = statusChecker;
    }
}
