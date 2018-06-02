package org.concordion.api;

import org.concordion.internal.FixtureType;

import java.io.PrintStream;

public interface ResultSummary {

    void assertIsSatisfied(FixtureType fixtureType);

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    long getIgnoredCount();

    void print(PrintStream out, FixtureType fixtureType);

    String printCountsToString(FixtureType fixtureType);

    String getSpecificationDescription();

    boolean isForExample();

    ImplementationStatus getImplementationStatus();
}
