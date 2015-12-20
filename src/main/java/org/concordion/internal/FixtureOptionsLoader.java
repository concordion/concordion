package org.concordion.internal;

import java.util.List;

import org.concordion.api.Fixture;
import org.concordion.api.option.ConcordionOptions;

public class FixtureOptionsLoader {

    public void addOptions(Fixture fixture, ConcordionBuilder concordionBuilder) {
        List<Class<?>> classes = fixture.getClassHierarchyParentFirst();
        for (Class<?> class1 : classes) {
            ConcordionOptions options = class1.getAnnotation(ConcordionOptions.class);
            if (options != null) {
                concordionBuilder.configureWith(options);
            }
        }
    }
}
