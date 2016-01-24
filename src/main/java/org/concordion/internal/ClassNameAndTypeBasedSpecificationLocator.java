package org.concordion.internal;

import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocatorWithType;
import org.concordion.internal.util.Check;

public class ClassNameAndTypeBasedSpecificationLocator extends ClassNameBasedSpecificationLocator implements SpecificationLocatorWithType {

    @Override
    public Resource locateSpecification(Object fixture, String typeSuffix) {
        Check.notNull(fixture, "Fixture is null");
        return FixtureSpecificationMapper.toSpecificationResource(fixture, typeSuffix);
    }
}
