package org.concordion.internal;

import java.util.List;

import org.concordion.api.option.ConcordionOptions;

public class FixtureOptionsLoader {
    public void addOptions(FixtureType fixtureType, ConcordionBuilder concordionBuilder) {
        List<ConcordionOptions> classAnnotations = fixtureType.getDeclaredConcordionOptionsParentFirst();
        for (ConcordionOptions options : classAnnotations) {
            concordionBuilder.configureWith(options);
        }
    }
}
