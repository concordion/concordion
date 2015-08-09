package org.concordion.internal.cache;

import org.concordion.api.Result;
import org.concordion.internal.SingleResultSummary;

/**
 * Created by tim on 30/06/15.
 */
class CacheResultSummary extends SingleResultSummary {

    public CacheResultSummary(Result result, String specificationDescription) {
        super(result, specificationDescription);
    }

    @Override
    public String printCountsToString(Object fixture, String example) {
        // no counts for cached result summary
        return null;
    }

}
