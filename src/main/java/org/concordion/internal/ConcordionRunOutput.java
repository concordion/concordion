package org.concordion.internal;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

/**
 * Data to store in the cache
 */
public class ConcordionRunOutput {
    private ResultSummary actualResultSummary;
    private ResultSummary postProcessedResultSummary;

    public ConcordionRunOutput(Class<?> fixtureClass) {
        SingleResultSummary singleResultSummary = new CacheResultSummary(Result.IGNORED,
                "No current results for fixture " + fixtureClass.getName() + " as the specification is currently being executed");
        this.actualResultSummary = singleResultSummary;
        this.postProcessedResultSummary = singleResultSummary;
    }

    public ResultSummary getActualResultSummary() {
        return actualResultSummary;
    }

    public void setActualResultSummary(ResultSummary actualResultSummary) {
        this.actualResultSummary = actualResultSummary;
    }

    public ResultSummary getPostProcessedResultSummary() {
        return postProcessedResultSummary;
    }

    public void setPostProcessedResultSummary(ResultSummary postProcessedResultSummary) {
        this.postProcessedResultSummary = postProcessedResultSummary;
    }
}
