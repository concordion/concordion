package org.concordion.internal;

import org.concordion.api.Fixture;
import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocatorWithType;

public class ClassNameAndTypeBasedSpecificationLocator extends ClassNameBasedSpecificationLocator implements SpecificationLocatorWithType {

    @Override
    public Resource locateSpecification(Object fixture, String typeSuffix) {
        return locateSpecification(new Fixture(fixture), typeSuffix);
    }
}
