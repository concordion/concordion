package org.concordion.internal.cache;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

/**
 * Data to store in the cache
 */
public class ConcordionRunOutput {
    private static final CacheResultSummary IN_PROGRESS_RESULT_SUMMARY = new CacheResultSummary(Result.IGNORED,
            "No current results as the specification is currently being executed");
    private ResultSummary actualResultSummary;
    private ResultSummary modifiedResultSummary;

    public ConcordionRunOutput(FixtureDeclarations fixture) {
        this.actualResultSummary = IN_PROGRESS_RESULT_SUMMARY;
        this.modifiedResultSummary = IN_PROGRESS_RESULT_SUMMARY;
    }

    public ConcordionRunOutput(ResultSummary actualResultSummary, ResultSummary modifiedResultSummary) {
        this.actualResultSummary = actualResultSummary;
        this.modifiedResultSummary = modifiedResultSummary;
    }

    public ResultSummary getActualResultSummary() {
        return actualResultSummary;
    }

    public void setActualResultSummary(ResultSummary actualResultSummary) {
        this.actualResultSummary = actualResultSummary;
    }

    public ResultSummary getModifiedResultSummary() {
        return modifiedResultSummary;
    }

    public void setModifiedResultSummary(ResultSummary modifiedResultSummary) {
        this.modifiedResultSummary = modifiedResultSummary;
    }
}
