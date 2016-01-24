package org.concordion.api;

/**
 * Locates a specification, allowing the type suffix of the specification to be specified.
 *
 * @since 2.0.0
 */
public interface SpecificationLocatorWithType {
    Resource locateSpecification(Object fixtureObject, String typeSuffix);
}
