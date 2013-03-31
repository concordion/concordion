package org.concordion.api;

import java.io.IOException;

public interface SpecificationReader {

    Specification readSpecification(Resource resource) throws IOException;

}
