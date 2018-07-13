package org.concordion.api;

import java.util.List;

import org.concordion.api.option.ConcordionOptions;
import org.concordion.internal.ResourceToCopy;

/**
 * The declaration of a fixture class.
 * 
 * @since 2.0.0
 */
public interface FixtureDeclarations {
    Class<?> getFixtureClass();

    boolean declaresFullOGNL();

    boolean declaresFailFast();

    Class<? extends Throwable>[] getDeclaredFailFastExceptions();

    boolean declaresResources();

    ImplementationStatus getDeclaredImplementationStatus();

    List<ConcordionOptions> getDeclaredConcordionOptionsParentFirst();

    String getFixturePathWithoutSuffix();

    String getDescription();
}