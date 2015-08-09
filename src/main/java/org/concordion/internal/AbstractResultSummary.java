package org.concordion.internal;

import java.io.PrintStream;

import org.concordion.api.ResultModifier;
import org.concordion.api.ResultSummary;

public abstract class AbstractResultSummary implements ResultSummary {

    private String specificationDescription = "";
    private ResultModifier resultModifier;

    public boolean isForExample() {
        return false;
    }

    public void print(PrintStream out, Object fixture, String example) {
        out.print(printToString(fixture, example));
    }

    public String printToString(Object fixture, String example) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(specificationDescription);
        builder.append("\n");
        String counts = printCountsToString(fixture, null);
        if (counts != null) {
            builder.append(counts).append("\n");
        }
//        builder.append("\n");
        return builder.toString();
    }

    public String printCountsToString(Object fixture, String example) {
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
            builder.append(FixtureState.getFixtureState(fixture.getClass(), this.getResultModifier()).printNoteToString());
        }

        return builder.toString();
    }



    public void setSpecificationDescription(String specificationDescription) {
        this.specificationDescription = specificationDescription;
    }

    public String getSpecificationDescription() {
        return specificationDescription;
    }

    public ResultModifier getResultModifier() {
        return resultModifier;
    }

    public void setResultModifier(ResultModifier resultModifier) {
        this.resultModifier = resultModifier;
    }
}
