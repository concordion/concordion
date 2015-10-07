package org.concordion.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;

public class SummarizingResultRecorder extends AbstractResultSummary implements ResultRecorder, ResultSummary {

    private final List<Result> recordedResults = new ArrayList<Result>();
    private FailFastException failFastException;
    private String specificationDescription;
    boolean forExample;

    public SummarizingResultRecorder() {
        this(null);
    }

    public SummarizingResultRecorder(String specificationDescription) {
        this.specificationDescription = specificationDescription;
        reset();
    }

    @Override
    public void record(Result result) {
        recordedResults.add(result);
    }

    private void recordMultipleResults(long number, Result type) {
		for (long i=0; i<number; i++) {
			record(type);
		}
    }

    @Override
	public void record(ResultSummary result) {
		recordMultipleResults(result.getSuccessCount(), Result.SUCCESS);
		recordMultipleResults(result.getFailureCount(), Result.FAILURE);
		recordMultipleResults(result.getIgnoredCount(), Result.IGNORED);
		recordMultipleResults(result.getExceptionCount(), Result.EXCEPTION);
	}

    @Override
    public void assertIsSatisfied(Fixture fixture) {
        ExpectedState state = getExpectedState(fixture);
        state.assertIsSatisfied(this, failFastException);
    }

    @Override
	public boolean hasExceptions() {
        return getExceptionCount() > 0;
    }

    private long getCount(Result result) {
        int count = 0;
        for (Result candidate : recordedResults) {
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
    public void print(PrintStream out, Fixture fixture) {
        out.print(printToString(fixture));
    }

    public String printToString(Fixture fixture) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(specificationDescription);
        builder.append("\n");
        String counts = printCountsToString(fixture);
    	if (counts != null) {
            builder.append(counts).append("\n");
        }
        return builder.toString();
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

    @Override
    public void setSpecificationDescription( String specificationDescription) {
        this.specificationDescription = specificationDescription;
    }

    @Override
    public String getSpecificationDescription() {
        return specificationDescription;
    }

    @Override
    public void setForExample(boolean isForExample) {
        this.forExample = isForExample;
    }

    @Override
    public boolean isForExample() {
        return forExample;
    }

    public long getTotalCount() {
        return recordedResults.size();
    }

    public final void reset() {
        recordedResults.clear();
        failFastException = null;
        forExample = false;
    }
}

