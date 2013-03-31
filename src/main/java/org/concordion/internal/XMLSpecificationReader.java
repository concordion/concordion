package org.concordion.internal;

import java.io.IOException;

import nu.xom.Document;

import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.api.Specification;
import org.concordion.api.SpecificationReader;

public class XMLSpecificationReader implements SpecificationReader {

    private final Source source;
    private final XMLParser xmlParser;
    private final DocumentParser documentParser;

    public XMLSpecificationReader(Source source, XMLParser xmlParser, DocumentParser documentParser) {
        this.source = source;
        this.xmlParser = xmlParser;
        this.documentParser = documentParser;
    }
    
    public Specification readSpecification(Resource resource) throws IOException {
        Document document = xmlParser.parse(source.createInputStream(resource), String.format("[%s: %s]", source, resource.getPath()));
        return documentParser.parse(document, resource);
    }
}
