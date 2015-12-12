package org.concordion.internal;

import org.concordion.api.Fixture;
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
    
    public Resource locateSpecification(Fixture fixture) {
        return locateSpecification(fixture, specificationSuffix);
    }

    @Override
    public Resource locateSpecification(Object fixture) {
        return locateSpecification(new Fixture(fixture), specificationSuffix);
    }

    protected Resource locateSpecification(Fixture fixture, String specificationSuffix) {
        Check.notNull(fixture, "Fixture is null");
        String fixturePath = fixture.getFixturePathWithoutSuffix();
        String resourcePath = "/" + fixturePath + "." + specificationSuffix;
        
        return new Resource(resourcePath);
    }
}
