package org.concordion.internal;

import java.io.PrintStream;

import org.concordion.api.ResultSummary;

public abstract class AbstractResultSummary implements ResultSummary {

    private String specificationDescription = "";

    @Override
    public String printToString(Object fixture) {
    	StringBuilder builder = new StringBuilder(specificationDescription);
    	builder.append("\n");
        String counts = printCountsToString(fixture);
    	if (counts != null) {
            builder.append(counts).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String printCountsToString(Object fixture) {
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
    
        builder.append(FixtureState.getFixtureState(fixture.getClass()).printNoteToString());
    
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
    @Deprecated
    public void print(PrintStream out) {
        print(out, this);
    }

    @Override
    public void print(PrintStream out, Object fixture) {
    	out.print(printToString(fixture));
    }
}
