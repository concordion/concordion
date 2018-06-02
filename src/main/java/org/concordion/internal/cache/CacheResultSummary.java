package org.concordion.internal.cache;

import org.concordion.api.Fixture;
import org.concordion.api.Result;
import org.concordion.internal.FixtureType;
import org.concordion.internal.SingleResultSummary;

class CacheResultSummary extends SingleResultSummary {

    public CacheResultSummary(Result result, String specificationDescription) {
        super(result, specificationDescription);
    }

    @Override
    public String printCountsToString(FixtureType fixtureType) {
        // no counts for cached result summary
        return null;
    }
}
