package org.concordion.internal;

import org.concordion.api.Resource;

public interface SpecificationDescriber {
    String getDescription(Resource resource);
    String getDescription(Resource resource, String exampleName);
}
