package org.concordion.internal;

import org.concordion.api.Resource;

public abstract class ClassNameAndTypeBasedSpecificationLocator extends ClassNameBasedSpecificationLocator {
    public abstract Resource locateSpecification(FixtureType fixtureType, String typeSuffix);
}
