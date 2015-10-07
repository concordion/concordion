package org.concordion.api;

import org.concordion.internal.Fixture;

public interface SpecificationLocator {

    Resource locateSpecification(Fixture fixture);
}
