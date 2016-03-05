package org.concordion.internal.cache;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.SingleResultSummary;

/**
 * Data to store in the cache
 */
public class ConcordionRunOutput {
    private ResultSummary actualResultSummary;
    private ResultSummary modifiedResultSummary;

    public ConcordionRunOutput(FixtureDeclarations fixture) {
        SingleResultSummary singleResultSummary = new CacheResultSummary(Result.IGNORED,
                "No current results for fixture " + fixture + " as the specification is currently being executed");
        this.actualResultSummary = singleResultSummary;
        this.modifiedResultSummary = singleResultSummary;
    }

    public ConcordionRunOutput(ResultSummary actualResultSummary, ResultSummary modifiedResultSummary) {
        super();
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
