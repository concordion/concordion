package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;

public class SummarizingResultRecorder extends AbstractResultSummary implements ResultRecorder, ResultSummary {

    private List<Result> recordedResults = new ArrayList<Result>();
    private FailFastException failFastException;
    public SummarizingResultRecorder() {

    }

    public SummarizingResultRecorder(ResultSummary initialSummary) {
        this();
        record(initialSummary);
    }

    @Override
	public void record( Result result) {
        recordedResults.add(result);
    }

    private void recordMultipleResults(long number, Result type) {
		for (long i=0; i<number; i++) {
			record(type);
		}
    }

	@Override
	public void record( ResultSummary result) {
		recordMultipleResults(result.getSuccessCount(), Result.SUCCESS);
		recordMultipleResults(result.getFailureCount(), Result.FAILURE);
		recordMultipleResults(result.getIgnoredCount(), Result.IGNORED);
		recordMultipleResults(result.getExceptionCount(), Result.EXCEPTION);
	}

    @Override @Deprecated
	public void assertIsSatisfied() {
        assertIsSatisfied(this);
    }

    @Override
	public void assertIsSatisfied(Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture.getClass());
        state.assertIsSatisfied(this, failFastException);
    }
    
    @Override
    public ResultSummary getMeaningfulResultSummary(Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture.getClass());
    	return state.getMeaningfulResultSummary(this, failFastException);
    }


    @Override
	public boolean hasExceptions() {
        return getExceptionCount() > 0;
    }

    public long getCount(Result result) {
        int count = 0;
        for ( Result candidate : recordedResults) {
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
    public void recordFailFastException( FailFastException exception) {
        this.setFailFastException(exception);
    }

    public FailFastException getFailFastException() {
        return failFastException;
    }

    public void setFailFastException( FailFastException exception) {
        this.failFastException = exception;
    }
}
