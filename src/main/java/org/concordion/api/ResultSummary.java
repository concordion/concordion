package org.concordion.api;

import java.io.PrintStream;

public interface ResultSummary {

    void assertIsSatisfied(FixtureDeclarations fixtureDeclarations);

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    long getIgnoredCount();

    void print(PrintStream out, FixtureDeclarations fixtureDeclarations);

    String printCountsToString(FixtureDeclarations fixtureDeclarations);

    String getSpecificationDescription();

    boolean isForExample();

    ImplementationStatus getImplementationStatus();
}
