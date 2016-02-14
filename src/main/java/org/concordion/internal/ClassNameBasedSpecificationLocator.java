package org.concordion.internal;

import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocator;
import org.concordion.internal.util.Check;

public class ClassNameBasedSpecificationLocator implements SpecificationLocator {

    private String specificationSuffix;

    public ClassNameBasedSpecificationLocator() {
        this("html");
    }
    
    public ClassNameBasedSpecificationLocator(String specificationSuffix) {
        this.specificationSuffix = specificationSuffix;
    }
    
    @Override
    public Resource locateSpecification(Object fixture) {
        Check.notNull(fixture, "Fixture is null");
        return FixtureSpecificationMapper.toSpecificationResource(fixture, specificationSuffix);
    }

    @Override
    public Resource locateSpecification(Object fixture, String typeSuffix) {
        Check.notNull(fixture, "Fixture is null");
        return FixtureSpecificationMapper.toSpecificationResource(fixture, typeSuffix);
    }
}
