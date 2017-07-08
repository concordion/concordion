package org.concordion.integration;

/**
 * An interface for integrating other test frameworks with Concordion.
 * Currently this is limited to determining whether a given class is a Concordion fixture, but is likely to be
 * extended in future.
 * <p>
 *     The providers are loaded using Java's Service Provider Interface. If creating your own instance, you will
 *     need to create a META-INF/services/org.concordion.integration.TestFrameworkProvider file containing the
 *     fully qualified name of your class, and include this file in your jar file.
 * </p>
 * @since 2.1.0
 */
public interface TestFrameworkProvider {
    boolean isConcordionFixture(Class<?> clazz);
}
