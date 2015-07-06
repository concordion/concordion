package org.concordion.api;

import java.io.PrintStream;

public interface ResultSummary {

    @Deprecated
    /* Use assertIsSatisfied(fixture) instead. */
    void assertIsSatisfied();

    @Deprecated
    void assertIsSatisfied(Object fixture);

    void assertIsSatisfied(Object fixture, String example);

    /*
     * In some cases (particularly fixtures marked with @ExpectedToFail or @Unimplemented) we need to
     * change the result summary to something that doesn't cause the parent fixture to fail when
     * the numbers of successes, failures, and exceptions are added up. This method gets the meaningful summary.
     */
    ResultSummary getMeaningfulResultSummary(Object fixture, String example);

    @Deprecated
    ResultSummary getMeaningfulResultSummary(Object fixture);

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    long getIgnoredCount();

    @Deprecated
    /* Use print(out, fixture, example) instead. */
    void print(PrintStream out);

    @Deprecated
    /* Use print(out, fixture, example) instead. */
    void print(PrintStream out, Object fixture);

    void print(PrintStream out, Object fixture, String example);

	String printToString(Object fixture);
    @Deprecated
    String printCountsToString(Object fixture);
    String printCountsToString(Object fixture, String example);

    String getSpecificationDescription();

    boolean isForExample();

    FixtureState getFixtureState();
}
