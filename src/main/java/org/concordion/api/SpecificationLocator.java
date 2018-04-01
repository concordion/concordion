package org.concordion.api;

public interface SpecificationLocator {
    /**
     * Locates a specification, allowing the type suffix of the specification to be specified.
     * @param fixtureObject the fixture to find the specification for
     * @param typeSuffix the suffix of the specification
     * @return the resource for the specification, which may or may not actually exist
     *
     * @since 2.0.0
     */
    Resource locateSpecification(Object fixtureObject, String typeSuffix);
}
