package org.concordion.internal;

import org.concordion.api.ResultSummary;

public class ConcordionAssertionError extends AssertionError {

	private static final long serialVersionUID = 1L;
	private final ResultSummary rs;


	public ConcordionAssertionError(String description, ResultSummary rs) {
		super(description);
		this.rs= rs;
		this.setStackTrace(new StackTraceElement[] {});
	}

	public ResultSummary getResultSummary() {
		return rs;
	}

}
