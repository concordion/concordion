package org.concordion.api;

import java.io.IOException;

public interface SpecificationReader {

    Specification readSpecification(Resource resource) throws IOException;

    boolean canFindSpecification(Resource resource) throws IOException;

    void setSpecificationConverter(SpecificationConverter converter);

    void setCopySourceHtmlTarget(Target target);
}
