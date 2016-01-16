package org.concordion.internal;

import java.util.List;

import org.concordion.api.FixtureOptions;
import org.concordion.api.option.ConcordionOptions;

public class FixtureOptionsLoader {
    public void addOptions(FixtureOptions fixture, ConcordionBuilder concordionBuilder) {
        List<ConcordionOptions> classAnnotations = fixture.getDeclaredConcordionOptionsParentFirst();
        for (ConcordionOptions options : classAnnotations) {
            concordionBuilder.configureWith(options);
        }
    }
}
