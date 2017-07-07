package org.concordion.api;

public interface SpecificationLocator {
    /**
     * @deprecated - use {@link #locateSpecification(Object, String)} instead  
     * Locates the specification for the named fixture, assuming a fixed type suffix (eg. HTML).
     * @param fixture the fixture to find the specification for
     * @return the resource for the specification, which may or may not actually exist
     */
    @Deprecated
    Resource locateSpecification(Object fixture);
    
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
