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

    private final List<Result> recordedResults = new ArrayList<Result>();
    private FailFastException failFastException;
    private String specificationDescription;

    @Override
	public void record(final Result result) {
        recordedResults.add(result);
    }

    private void recordMultipleResults(final long number, final Result type) {
		for (long i=0; i<number; i++) {
			record(type);
		}
    }

	@Override
	public void record(final ResultSummary result) {
		recordMultipleResults(result.getSuccessCount(), Result.SUCCESS);
		recordMultipleResults(result.getFailureCount(), Result.FAILURE);
		recordMultipleResults(result.getIgnoredCount(), Result.IGNORED);
		recordMultipleResults(result.getExceptionCount(), Result.EXCEPTION);
	}

    @Override
	public void assertIsSatisfied() {
        assertIsSatisfied(this);
    }

    @Override
	public void assertIsSatisfied(final Object fixture) {
        final FixtureState state = getFixtureState(fixture);
        state.assertIsSatisfied(getSuccessCount(), getFailureCount(), getExceptionCount(), failFastException);
    }

    private FixtureState getFixtureState(final Object fixture) {
        FixtureState state = FixtureState.EXPECTED_TO_PASS;
        if (fixture.getClass().getAnnotation(ExpectedToFail.class) != null) {
            state = FixtureState.EXPECTED_TO_FAIL;
        }
        if (fixture.getClass().getAnnotation(Unimplemented.class) != null) {
            state = FixtureState.UNIMPLEMENTED;
        }
        return state;
    }

    @Override
	public boolean hasExceptions() {
        return getExceptionCount() > 0;
    }

    public long getCount(final Result result) {
        int count = 0;
        for (final Result candidate : recordedResults) {
            if (candidate == result) {
                count++;
            }
        }
        return count;
    }

    @Override
	public long getExceptionCount() {
        return getCount(Result.EXCEPTION);
    }

    @Override
	public long getFailureCount() {
        return getCount(Result.FAILURE);
    }

    @Override
	public long getSuccessCount() {
        return getCount(Result.SUCCESS);
    }

    @Override
	public long getIgnoredCount() {
        return getCount(Result.IGNORED);
    }

    @Override
	public void print(final PrintStream out) {
        print(out, this);
    }

    @Override
	public void print(final PrintStream out, final Object fixture) {
        out.println(specificationDescription);
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

    @Override
    public void recordFailFastException(final FailFastException exception) {
        this.setFailFastException(exception);
    }

    public Throwable getFailFastException() {
        return failFastException;
    }

    public void setFailFastException(final FailFastException exception) {
        this.failFastException = exception;
    }

    @Override
    public void setSpecificationDescription(final String specificationDescription) {
        this.specificationDescription = specificationDescription;
    }
}
