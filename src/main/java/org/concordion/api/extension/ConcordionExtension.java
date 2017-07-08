package org.concordion.api.extension;

/**
 * Supplements Concordion behaviour by adding new commands, listeners or output enhancements.
 * <p>
 * To use extensions, set the system property <code>concordion.extensions</code> to a comma-separated list containing:
 * <ul>
 * <li>the fully-qualified class name of extensions to be installed, and/or</li>
 * <li>the fully-qualified class name of extension factories that will create an extension.</li>
 * </ul>
 * <p>
 * If an extension is specified, it will be instantiated by Concordion. 
 * <p>
 * All extensions and/or extension factories must be present on the classpath. 
 * Extensions must implement {@link ConcordionExtension}. 
 * Extension factories must implement {@link ConcordionExtensionFactory}.
 */
public interface ConcordionExtension {
    void addTo(ConcordionExtender concordionExtender);
}
