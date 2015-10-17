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

    @Override @Deprecated
    public void print(PrintStream out, Object fixture) {
        print(out, new Fixture(fixture));
    }

    @Override
    public void print(PrintStream out, Fixture fixture) {
        out.print(printToString(fixture));
    }

    String printToString(Fixture fixture) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(specificationDescription);
        builder.append("\n");
        String counts = printCountsToString(fixture);
        if (counts != null) {
            builder.append(counts).append("\n");
        }
//        builder.append("\n");
        return builder.toString();
    }

    @Override @Deprecated
    public String printCountsToString(Object fixture) {
        return printCountsToString(new Fixture(fixture));
    }

    @Override
    public String printCountsToString(Fixture fixture) {
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

        builder.append(getImplementationStatusChecker(fixture).printNoteToString());

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
    
    public ImplementationStatusChecker getImplementationStatusChecker(Fixture fixture) {
        ImplementationStatus implementationStatus;
        if (isForExample()) {
            implementationStatus = getImplementationStatus();
        } else {
            implementationStatus = fixture.getImplementationStatus();
        }
        return ImplementationStatusChecker.implementationStatusCheckerFor(implementationStatus);
    }

    @Override @Deprecated
    public void assertIsSatisfied(Object fixture) {
        assertIsSatisfied(new Fixture(fixture));
    }
}
