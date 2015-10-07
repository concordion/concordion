package org.concordion.api;

import java.io.PrintStream;

import org.concordion.internal.Fixture;

public interface ResultSummary {

    void assertIsSatisfied(Fixture fixture);

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    long getIgnoredCount();

    void print(PrintStream out, Fixture fixture);

    String printCountsToString(Fixture fixture);

    String getSpecificationDescription();

    boolean isForExample();

    ResultModifier getResultModifier();
}
