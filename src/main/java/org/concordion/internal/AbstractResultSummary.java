package org.concordion.internal;

import java.io.PrintStream;

import org.concordion.api.Fixture;
import org.concordion.api.ImplementationStatus;
import org.concordion.api.ResultSummary;

public abstract class AbstractResultSummary implements ResultSummary {

    private String specificationDescription = "";
    private ImplementationStatus implementationStatus;

    @Override
    public boolean isForExample() {
        return false;
    }

    @Override
    public void print(PrintStream out, FixtureType fixtureType) {
        out.print(printToString(fixtureType));
    }

    String printToString(FixtureType fixtureType) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(specificationDescription);
        builder.append("\n");
        String counts = printCountsToString(fixtureType);
        if (counts != null) {
            builder.append(counts).append("\n");
        }
//        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String printCountsToString(FixtureType fixtureType) {
        StringBuilder builder = new StringBuilder();

        builder.append("Successes: ");
        builder.append(getSuccessCount());
        builder.append(", Failures: ");
        builder.append(getFailureCount());
        if (getIgnoredCount() > 0) {
            builder.append(", Ignored: ");
            builder.append(getIgnoredCount());
        }
        if (hasExceptions()) {
            builder.append(", Exceptions: ");
            builder.append(getExceptionCount());
        }

        builder.append(getImplementationStatusChecker(fixtureType).printNoteToString());

        return builder.toString();
    }

    public void setSpecificationDescription(String specificationDescription) {
        this.specificationDescription = specificationDescription;
    }

    @Override
    public String getSpecificationDescription() {
        return specificationDescription;
    }

    @Override
    public ImplementationStatus getImplementationStatus() {
        return implementationStatus;
    }

    public void setImplementationStatus(ImplementationStatus implementationStatus) {
        this.implementationStatus = implementationStatus;
    }
    
    public ImplementationStatusChecker getImplementationStatusChecker(FixtureType fixtureType) {
        ImplementationStatus implementationStatus;
        if (isForExample() || fixtureType == null) {
            implementationStatus = getImplementationStatus();
        } else {
            implementationStatus = fixtureType.getDeclaredImplementationStatus();
        }
        return ImplementationStatusChecker.implementationStatusCheckerFor(implementationStatus);
    }
}
