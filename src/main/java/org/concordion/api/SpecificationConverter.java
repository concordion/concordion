package org.concordion.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Converts specifications from one type to another. For example, from Markdown to HTML.
 * 
 * @since 2.0.0
 */
public interface SpecificationConverter {

    /**
     * Convert the specification's input stream to an HTML input stream.
     * @param inputStream the input stream containing the content of the specification
     * @param specificationName the filename of the specification (without the path)
     * @return converted specification content
     * @throws IOException on i/o error
     */
    InputStream convert(InputStream inputStream, String specificationName) throws IOException;
}
