package org.concordion.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Converts specifications from one type to another. For example, from Markdown to HTML.
 * 
 * @since 2.0.0
 */
public interface SpecificationConverter {

    InputStream convert(Resource resource, InputStream inputStream) throws IOException;
}
