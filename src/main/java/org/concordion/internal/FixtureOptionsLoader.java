package org.concordion.internal;

import java.util.List;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.option.ConcordionOptions;

public class FixtureOptionsLoader {
    public void addOptions(FixtureDeclarations fixtureDeclarations, ConcordionBuilder concordionBuilder) {
        List<ConcordionOptions> classAnnotations = fixtureDeclarations.getDeclaredConcordionOptionsParentFirst();
        for (ConcordionOptions options : classAnnotations) {
            concordionBuilder.configureWith(options);
        }
    }
}
