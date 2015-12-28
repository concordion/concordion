package org.concordion.api;

import java.io.IOException;
import java.io.InputStream;

public interface SpecificationConverter {

    InputStream convert(Resource resource, InputStream inputStream) throws IOException;
}
