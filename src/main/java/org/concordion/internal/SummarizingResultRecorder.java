package org.concordion.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;
import org.concordion.api.Unimplemented;

public class SummarizingResultRecorder implements ResultRecorder, ResultSummary {

    private List<Result> recordedResults = new ArrayList<Result>();

    public void record(Result result) {
        recordedResults.add(result);
    }
    
    public void assertIsSatisfied() {
        assertIsSatisfied(this);
    }

    public void assertIsSatisfied(Object fixture) {
        FixtureState state = getFixtureState(fixture);
        state.assertIsSatisfied(getSuccessCount(), getFailureCount(), getExceptionCount());
    }

    private FixtureState getFixtureState(Object fixture) {
        FixtureState state = FixtureState.EXPECTED_TO_PASS;
        if (fixture.getClass().getAnnotation(ExpectedToFail.class) != null) {
            state = FixtureState.EXPECTED_TO_FAIL;
        }
        if (fixture.getClass().getAnnotation(Unimplemented.class) != null) {
            state = FixtureState.UNIMPLEMENTED;
        }
        return state;
    }
    
    public boolean hasExceptions() {
        return getExceptionCount() > 0;
    }

    public long getCount(Result result) {
        int count = 0;
        for (Result candidate : recordedResults) {
            if (candidate == result) {
                count++;
            }
        }
        return count;
    }

    public long getExceptionCount() {
        return getCount(Result.EXCEPTION);
    }

    public long getFailureCount() {
        return getCount(Result.FAILURE);
    }

    public long getSuccessCount() {
        return getCount(Result.SUCCESS);
    }

    public long getIgnoredCount() {
        return getCount(Result.IGNORED);
    }

    public void print(PrintStream out) {
        print(out, this);
    }

    public void print(PrintStream out, Object fixture) {
        out.print("Successes: " + getSuccessCount());
        out.print(", Failures: " + getFailureCount());
        if (getIgnoredCount() > 0) {
            out.print(", Ignored: " + getIgnoredCount());
        }
        if (hasExceptions()) {
            out.print(", Exceptions: " + getExceptionCount());
        }
        getFixtureState(fixture).printNote(out);
        out.println("\n");
    }
}
