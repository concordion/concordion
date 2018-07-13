package org.concordion.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.concordion.api.*;

public class SummarizingResultRecorder extends AbstractResultSummary implements ResultRecorder, ResultSummary {

    private final List<Result> recordedResults = Collections.synchronizedList(new ArrayList<Result>());
    private FailFastException failFastException;
    private boolean forExample;

    public SummarizingResultRecorder() {
        this(null);
    }

    public SummarizingResultRecorder(String specificationDescription) {
        this.setSpecificationDescription(specificationDescription);
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
    public void assertIsSatisfied(FixtureDeclarations fixtureDeclarations) {
        getImplementationStatusChecker(fixtureDeclarations).assertIsSatisfied(this, failFastException);
    }

    @Override
	public boolean hasExceptions() {
        return getExceptionCount() > 0;
    }

    private long getCount(Result result) {

        // clone the list for the iterator
        List<Result> listCopy = new ArrayList<Result>(recordedResults);

        int count = 0;
        for (Result candidate : listCopy) {
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

