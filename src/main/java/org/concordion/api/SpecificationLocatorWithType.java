package org.concordion.api;

public interface SpecificationLocatorWithType {
    Resource locateSpecification(Object fixtureObject, String typeSuffix);
}
