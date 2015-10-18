package org.concordion.api;


public interface SpecificationLocator {

    /**
     * @deprecated  As of release 2.0, replaced by {@link #locateSpecification(Fixture)}
     */
    @Deprecated Resource locateSpecification(Object fixture);

    Resource locateSpecification(Fixture fixture);
}
