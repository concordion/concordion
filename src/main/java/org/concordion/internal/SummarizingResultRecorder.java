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
    private FailFastException failFastException;
    private String specificationDescription = "";

    public SummarizingResultRecorder() {

    }

    public SummarizingResultRecorder(ResultSummary initialSummary) {
        this();
        record(initialSummary);
    }

	public void record( Result result) {
        recordedResults.add(result);
    }

    private void recordMultipleResults(long number, Result type) {
		for (long i=0; i<number; i++) {
			record(type);
		}
    }

	public void record( ResultSummary result) {
		recordMultipleResults(result.getSuccessCount(), Result.SUCCESS);
		recordMultipleResults(result.getFailureCount(), Result.FAILURE);
		recordMultipleResults(result.getIgnoredCount(), Result.IGNORED);
		recordMultipleResults(result.getExceptionCount(), Result.EXCEPTION);
	}

    @Deprecated
	public void assertIsSatisfied() {
        assertIsSatisfied(this);
    }

	public void assertIsSatisfied(Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture.getClass());
        state.assertIsSatisfied(this, failFastException);
    }
    
    public ResultSummary getMeaningfulResultSummary(Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture.getClass());
    	return state.getMeaningfulResultSummary(this, failFastException);
    }


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

    @Deprecated
	public void print( PrintStream out) {
        print(out, this);
    }

	public void print(PrintStream out, Object fixture) {
    	out.print(printToString(fixture));
    }
    
    public String printToString(Object fixture) {
    	StringBuilder builder = new StringBuilder(specificationDescription);
    	builder.append("\n");
        String counts = printCountsToString(fixture);
    	if (counts != null) {
            builder.append(counts).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }
    	
    public String printCountsToString(Object fixture) {
    	StringBuilder builder = new StringBuilder();

        builder.append("Successes: ");
        builder.append(getSuccessCount());
        builder.append(", Failures: ");
        builder.append(getFailureCount());
        if (getIgnoredCount() > 0) {
        	builder.append(", Ignored: ");
        	builder.append(getIgnoredCount());
        }
        if (hasExceptions()) {
        	builder.append(", Exceptions: ");
        	builder.append(getExceptionCount());
        }

        builder.append(FixtureState.getFixtureState(fixture.getClass()).printNoteToString());

        return builder.toString();
    }


    public void recordFailFastException( FailFastException exception) {
        this.setFailFastException(exception);
    }

    public FailFastException getFailFastException() {
        return failFastException;
    }

    public void setFailFastException( FailFastException exception) {
        this.failFastException = exception;
    }

    public void setSpecificationDescription( String specificationDescription) {
        this.specificationDescription = specificationDescription;
    }

    public String getSpecificationDescription() {
        return specificationDescription;
    }
}
