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

    public SingleResultSummary(ResultSummary resultSummary) {
        // exceptions always override
        if (resultSummary.getExceptionCount() > 0) {
            result = Result.EXCEPTION;
        } else if (resultSummary.getFailureCount() > 0) {
            // a single failure makes the whole thing fail

            result = Result.FAILURE;
        } else if (resultSummary.getSuccessCount() > 0) {
            // check success count before ignore count - as if there is a single successfull test
            // then the result summary is not completly ignored.

            result = Result.SUCCESS;
        } else if (resultSummary.getIgnoredCount() > 0) {
            result = Result.IGNORED;
        } else {

            // result summary has no tests in it.
            result = Result.SUCCESS;
        }
        record(result);
    }

//    public ResultSummary getMeaningfulResultSummary(Object fixture, String example) {
//        return super.getMeaningfulResultSummary(fixture, example);
//    }

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
