package org.concordion.internal;

import java.io.PrintStream;

import org.concordion.api.FixtureDeclarations;
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
    public void print(PrintStream out, FixtureDeclarations fixtureDeclarations) {
        out.print(printToString(fixtureDeclarations));
    }

    String printToString(FixtureDeclarations fixtureDeclarations) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(specificationDescription);
        builder.append("\n");
        String counts = printCountsToString(fixtureDeclarations);
        if (counts != null) {
            builder.append(counts).append("\n");
        }
//        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String printCountsToString(FixtureDeclarations fixtureDeclarations) {
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

        builder.append(getImplementationStatusChecker(fixtureDeclarations).printNoteToString());

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
    
    public ImplementationStatusChecker getImplementationStatusChecker(FixtureDeclarations fixtureDeclarations) {
        ImplementationStatus implementationStatus;
        if (isForExample() || fixtureDeclarations == null) {
            implementationStatus = getImplementationStatus();
        } else {
            implementationStatus = fixtureDeclarations.getDeclaredImplementationStatus();
        }
        return ImplementationStatusChecker.implementationStatusCheckerFor(implementationStatus);
    }
}
