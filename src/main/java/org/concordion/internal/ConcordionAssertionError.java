package org.concordion.internal;

import org.concordion.api.ResultSummary;

public class ConcordionAssertionError extends AssertionError {

	private static final long serialVersionUID = 1L;
	private final ResultSummary rs;


	public ConcordionAssertionError(String description, ResultSummary rs) {
		super(description);
		this.rs= rs;
	}

	public ResultSummary getResultSummary() {
		return rs;
	}

	public long getNumSuccesses() {
		return rs.getSuccessCount();
	}

	public long getNumFailures() {
		return rs.getFailureCount();
	}

	public long getNumExceptions() {
		return rs.getExceptionCount();
	}

	public long getNumIgnores() {
		return rs.getIgnoredCount();
	}
}
