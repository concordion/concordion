package org.concordion.internal;

/**
 * Created by TimW5 on 15/11/14.
 *
 * @author TimW5
 */
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

public class SingleResultSummary extends SummarizingResultRecorder {
    private final Result result;

    public SingleResultSummary(final Result result) {
        this.record(result);
        this.result = result;
    }

    @Override
    public ResultSummary getMeaningfulResultSummary(Object fixture) {
        return super.getMeaningfulResultSummary(fixture);
    }

    public SingleResultSummary(final Result result, String specificationDescription) {
        this.record(result);
        this.result = result;

        this.setSpecificationDescription(specificationDescription);

    }

    public Result getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SingleResultSummary) {
            return this.result == ((SingleResultSummary)o).getResult();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return result.hashCode();
    }
}
