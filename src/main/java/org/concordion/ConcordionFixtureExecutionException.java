package org.concordion;


public class ConcordionFixtureExecutionException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Exception rootException;

	public ConcordionFixtureExecutionException(Exception rootException) {
		this.rootException = rootException;
	}

	public Exception getParsingException() {
		return rootException;
	}

}
