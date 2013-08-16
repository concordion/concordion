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
    
    public Resource locateSpecification(Object fixture) {
        Check.notNull(fixture, "Fixture is null");
        
        String dottedClassName = fixture.getClass().getName();
        String slashedClassName = dottedClassName.replaceAll("\\.", "/");
        String specificationName = slashedClassName.replaceAll("(Fixture|Test)$", "");
        String resourcePath = "/" + specificationName + "." + specificationSuffix;
        
        return new Resource(resourcePath);
    }
}
