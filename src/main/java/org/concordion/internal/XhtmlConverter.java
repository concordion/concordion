package org.concordion.internal;

import java.io.IOException;
import java.io.InputStream;

import org.concordion.api.SpecificationConverter;

public class XhtmlConverter implements SpecificationConverter {

    @Override
    public InputStream convert(InputStream inputStream, String specificationName) throws IOException {
        return inputStream;
    }
}
