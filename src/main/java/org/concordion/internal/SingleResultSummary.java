package org.concordion.internal;

/**
 * Created by TimW5 on 15/11/14.
 *
 * @author TimW5
 */
import org.concordion.api.Result;
import org.concordion.api.ResultModifier;
import org.concordion.api.ResultSummary;

import java.io.PrintStream;

public class SingleResultSummary extends AbstractResultSummary implements ResultSummary {
    private final Result result;

    public SingleResultSummary(Result result) {
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
        setResultModifier(resultSummary.getResultModifier());
    }

//    public ResultSummary getMeaningfulResultSummary(Object fixture, String example) {
//        return super.getMeaningfulResultSummary(fixture, example);
//    }

    public SingleResultSummary(final Result result, String specificationDescription) {
        this.result = result;
        this.setSpecificationDescription(specificationDescription);
    }
    
    public ResultSummary getMeaningfulResultSummary(Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture.getClass(), getResultModifier());
        return state.getMeaningfulResultSummary(this, null);
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

    public void assertIsSatisfied() {
        assertIsSatisfied(this);
    }

    public void assertIsSatisfied(Object fixture) {
        assertIsSatisfied(fixture, null);
    }


    public void assertIsSatisfied(Object fixture, String example) {
        FixtureState state = FixtureState.getFixtureState(fixture.getClass(), getResultModifier());
        state.assertIsSatisfied(this, null);
    }

    public boolean hasExceptions() {
        return result == Result.EXCEPTION;
    }

    public long getSuccessCount() {
        return result == Result.SUCCESS ? 1 : 0;
    }

    public long getFailureCount() {
        return result == Result.FAILURE ? 1 : 0;
    }

    public long getExceptionCount() {
        return result == Result.EXCEPTION ? 1 : 0;
    }

    public long getIgnoredCount() {
        return result == Result.IGNORED ? 1 : 0;
    }


}
