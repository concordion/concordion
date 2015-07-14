package org.concordion.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ResultModifier;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;

public class SummarizingResultRecorder implements ResultRecorder, ResultSummary {

    private List<Result> recordedResults = new ArrayList<Result>();
    private FailFastException failFastException;
    private String specificationDescription = "";
    boolean forExample = false;
    private ResultModifier resultModifier;

    public SummarizingResultRecorder() {
        this(null);
    }

    public SummarizingResultRecorder(String specificationDescription) {
        this.specificationDescription = specificationDescription;
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

    @Deprecated
    public void assertIsSatisfied(Object fixture) {
        assertIsSatisfied(fixture, null);
    }

    public void assertIsSatisfied(Object fixture, String example) {
        // only pass the example name through if this is an actual example - not any stray tests in the
        // spec
        FixtureState state = FixtureState.getFixtureState(
                fixture.getClass(),
                isForExample() ? this.getResultModifier() : null);
        state.assertIsSatisfied(this, failFastException);
    }

    @Deprecated
    public ResultSummary getMeaningfulResultSummary(Object fixture) {
        return getMeaningfulResultSummary(fixture, null);
    }

    private ResultSummary getMeaningfulResultSummary(Object fixture, String example) {
        // we pass null for the example if this is not an example. That lets the fixture state
        // use class annotations instead of the example tags.
        FixtureState state = FixtureState.getFixtureState(
                fixture.getClass(),
                isForExample() ? this.getResultModifier() : null);

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

    @Deprecated
	public void print(PrintStream out, Object fixture) {
        print(out, fixture, null);
    }

    public void print(PrintStream out, Object fixture, String example) {
        out.print(printToString(fixture, example));
    }

    @Deprecated
    public String printToString(Object fixture) {
        return printToString(fixture, null);
    }

    public String printToString(Object fixture, String example) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(specificationDescription);
        builder.append("\n");
        String counts = printCountsToString(fixture);
    	if (counts != null) {
            builder.append(counts).append("\n");
        }
//        builder.append("\n");
        return builder.toString();
    }

    @Deprecated
    public String printCountsToString(Object fixture) {
        return printCountsToString(fixture, null);
    }

    public String printCountsToString(Object fixture, String example) {
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

        if (fixture != null) {
            builder.append(FixtureState.getFixtureState(fixture.getClass(), this.getResultModifier()).printNoteToString());
        }

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

    public void setForExample(boolean isForExample) {
        this.forExample = isForExample;
    }

    public boolean isForExample() {
        return forExample;
    }

    public long getTotalCount() {
        return getSuccessCount() + getFailureCount() + getExceptionCount() + getIgnoredCount();
    }

    public ResultModifier getResultModifier() {
        return resultModifier;
    }

    public void setResultModifier(ResultModifier resultModifier) {
        this.resultModifier = resultModifier;
    }
}
