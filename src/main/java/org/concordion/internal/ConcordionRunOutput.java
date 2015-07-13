package org.concordion.internal;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

/**
 * Data to store in the cache
 */
public class ConcordionRunOutput {
    private final ResultSummary actualResultSummary;
    private final ResultSummary postProcessedResultSummary;

    public ConcordionRunOutput(Class<?> fixtureClass) {
        ResultSummary resultSummary = new CacheResultSummary(Result.IGNORED,
                "No current results for fixture " + fixtureClass.getName() + " as the specification is currently being executed");
        this.actualResultSummary = resultSummary;
        this.postProcessedResultSummary = resultSummary;
    }

    public ConcordionRunOutput(Class<?> fixtureClass, ResultSummary actualResultSummary, ResultSummary postProcessedResultSummary) {
        this.actualResultSummary = actualResultSummary;
        this.postProcessedResultSummary = postProcessedResultSummary;
    }
    
    public ResultSummary getActualResultSummary() {
        return actualResultSummary;
    }

    public ResultSummary getPostProcessedResultSummary() {
        return postProcessedResultSummary;
    }
}
