package org.concordion.api;

import java.io.PrintStream;

public interface ResultSummary {

    /**
     * @deprecated  As of release 2.0, replaced by {@link #assertIsSatisfied(Fixture fixture)}
     */
    @Deprecated void assertIsSatisfied(Object fixture);

    void assertIsSatisfied(Fixture fixture);

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    long getIgnoredCount();

    /**
     * @deprecated  As of release 2.0, replaced by {@link #print(PrintStream, Fixture)}
     */
    @Deprecated void print(PrintStream out, Object fixture);

    void print(PrintStream out, Fixture fixture);

    /**
     * @deprecated  As of release 2.0, replaced by {@link #printCountsToString(Fixture)}
     */
    @Deprecated String printCountsToString(Object fixture);

    String printCountsToString(Fixture fixture);

    String getSpecificationDescription();

    boolean isForExample();

    ImplementationStatus getImplementationStatus();
}
