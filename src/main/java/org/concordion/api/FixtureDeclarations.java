package org.concordion.api;

import java.util.List;

import org.concordion.api.option.ConcordionOptions;

/**
 * The declaration of a fixture class.
 * 
 * @since 2.0.0
 */
public interface FixtureDeclarations {
    boolean declaresFullOGNL();

    boolean declaresFailFast();
    
    Class<? extends Throwable>[] getDeclaredFailFastExceptions();

    boolean declaresResources();

    ImplementationStatus getDeclaredImplementationStatus();

    List<ConcordionOptions> getDeclaredConcordionOptionsParentFirst();
}