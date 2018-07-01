package org.concordion.internal;

import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocator;
import org.concordion.internal.util.Check;

public class ClassNameBasedSpecificationLocator implements SpecificationLocator {

    public ClassNameBasedSpecificationLocator() {
    }

    @Deprecated
    public ClassNameBasedSpecificationLocator(String specificationSuffix) {
    }

    @Override
    public Resource locateSpecification(FixtureType fixtureType, String specificationSuffix) {
        Check.notNull(fixtureType, "Fixture is null");
        return FixtureSpecificationMapper.toSpecificationResource(fixtureType, specificationSuffix);
    }
}
