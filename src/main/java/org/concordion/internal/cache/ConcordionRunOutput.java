package org.concordion.internal.cache;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.SingleResultSummary;

/**
 * Data to store in the cache
 */
public class ConcordionRunOutput {
    private ResultSummary actualResultSummary;
    private ResultSummary modifiedResultSummary;

    public ConcordionRunOutput(Class<?> fixtureClass) {
        SingleResultSummary singleResultSummary = new CacheResultSummary(Result.IGNORED,
                "No current results for fixture " + fixtureClass.getName() + " as the specification is currently being executed");
        this.actualResultSummary = singleResultSummary;
        this.modifiedResultSummary = singleResultSummary;
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
