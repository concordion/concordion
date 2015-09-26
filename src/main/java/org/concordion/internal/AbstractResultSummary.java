package org.concordion.internal;

import java.io.PrintStream;

import org.concordion.api.ResultModifier;
import org.concordion.api.ResultSummary;

public abstract class AbstractResultSummary implements ResultSummary {

    private String specificationDescription = "";
    private ResultModifier resultModifier;

    @Override
    public boolean isForExample() {
        return false;
    }

    @Override
    public void print(PrintStream out, Fixture fixture) {
        out.print(printToString(fixture));
    }

    private String printToString(Fixture fixture) {
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

        if (fixture != null) {
            builder.append(Fixture.getFixtureState(this.getResultModifier(), fixture).printNoteToString());
        }

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
    public ResultModifier getResultModifier() {
        return resultModifier;
    }

    public void setResultModifier(ResultModifier resultModifier) {
        this.resultModifier = resultModifier;
    }
}
