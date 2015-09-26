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
    
    public Resource locateSpecification(Fixture fixture) {
        Check.notNull(fixture, "Fixture is null");
        
        String specificationName = fixture.getShortenedFixtureName();
        String resourcePath = "/" + specificationName + "." + specificationSuffix;
        
        return new Resource(resourcePath);
    }
}
