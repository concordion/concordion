package org.concordion.internal;

import org.concordion.api.FixtureDeclarations;
import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocator;
import org.concordion.internal.util.Check;

public class ClassNameBasedSpecificationLocator implements SpecificationLocator {

    public ClassNameBasedSpecificationLocator() {
    }

    @Override
    public Resource locateSpecification(FixtureDeclarations fixtureDeclarations, String specificationSuffix) {
        Check.notNull(fixtureDeclarations, "Fixture is null");
        return FixtureSpecificationMapper.toSpecificationResource(fixtureDeclarations, specificationSuffix);
    }
}
