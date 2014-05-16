package org.concordion.internal;

public class ConcordionAssertionError extends AssertionError {

	private static final long serialVersionUID = 1L;
	private final long numSuccesses;
	private final long numFailures;
	private final long numExceptions;

	public ConcordionAssertionError(final String message, final long numSuccesses, final long numFailures, final long numExceptions) {
		super (message);
		this.numSuccesses = numSuccesses;
		this.numFailures = numFailures;
		this.numExceptions = numExceptions;
	}

	public long getNumSuccesses() {
		return numSuccesses;
	}

	public long getNumFailures() {
		return numFailures;
	}

	public long getNumExceptions() {
		return numExceptions;
	}

}
