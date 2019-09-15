package org.concordion.internal;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

public class SingleResultSummary extends AbstractResultSummary implements ResultSummary {
    private final Result result;

    public SingleResultSummary(Result result) {
        this.result = result;
    }

    public SingleResultSummary(final Result result, String specificationDescription) {
        this.result = result;
        this.setSpecificationDescription(specificationDescription);
    }

    public Result getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SingleResultSummary) {
            return this.result == ((SingleResultSummary)o).getResult();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return result.hashCode();
    }

    @Override
    public void assertIsSatisfied(FixtureDeclarations fixtureDeclarations) {
        getImplementationStatusChecker(fixtureDeclarations).assertIsSatisfied(this, null);
    }

    @Override
    public boolean hasExceptions() {
        return result == Result.EXCEPTION;
    }

    @Override
    public long getSuccessCount() {
        return result == Result.SUCCESS ? 1 : 0;
    }

    @Override
    public long getFailureCount() {
        return result == Result.FAILURE ? 1 : 0;
    }

    @Override
    public long getExceptionCount() {
        return result == Result.EXCEPTION ? 1 : 0;
    }

    @Override
    public long getIgnoredCount() {
        return result == Result.IGNORED ? 1 : 0;
    }
}
