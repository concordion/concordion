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

    @Override
	public void assertIsSatisfied() {
        assertIsSatisfied(this);
    }

    @Override
	public void assertIsSatisfied( Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture);
        state.assertIsSatisfied(this, failFastException);
    }
    
    @Override
    public ResultSummary getMeaningfulResultSummary(Object fixture) {
        FixtureState state = FixtureState.getFixtureState(fixture);
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
	public void print( PrintStream out) {
        print(out, this);
    }

    @Override
	public void print(PrintStream out, Object fixture) {
    	out.print(printToString(fixture));
    }
    
    @Override
    public String printToString(Object fixture) {
    	StringBuilder builder = new StringBuilder(specificationDescription);
    	builder.append("\n");
    	builder.append(printCountsToString(fixture));
        builder.append("\n");
        return builder.toString();
    }
    	
    @Override
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

        builder.append(FixtureState.getFixtureState(fixture).printNoteToString());
        
        return builder.toString();
    }


    @Override
    public void recordFailFastException( FailFastException exception) {
        this.setFailFastException(exception);
    }

    public Throwable getFailFastException() {
        return failFastException;
    }

    public void setFailFastException( FailFastException exception) {
        this.failFastException = exception;
    }

    @Override
    public void setSpecificationDescription( String specificationDescription) {
        this.specificationDescription = specificationDescription;
    }
}
