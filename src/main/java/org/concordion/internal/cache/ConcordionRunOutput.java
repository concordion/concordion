package org.concordion.internal.cache;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ImplementationStatusChecker;

/**
 * Data to store in the cache
 */
public class ConcordionRunOutput {
    private static final CacheResultSummary IN_PROGRESS_RESULT_SUMMARY = new CacheResultSummary(Result.IGNORED,
            "No current results as the specification is currently being executed");
    private ResultSummary actualResultSummary;
    private ImplementationStatusChecker statusChecker;

    public ConcordionRunOutput(FixtureDeclarations fixture) {
        this.actualResultSummary = IN_PROGRESS_RESULT_SUMMARY;
    }

    public ResultSummary getActualResultSummary() {
        return actualResultSummary;
    }

    public void setActualResultSummary(ResultSummary actualResultSummary) {
        this.actualResultSummary = actualResultSummary;
    }

    public ResultSummary getModifiedResultSummary() {
        return statusChecker.convertForCache(getActualResultSummary());
    }

    public void setStatusChecker(ImplementationStatusChecker statusChecker) {
        this.statusChecker = statusChecker;
    }
}
