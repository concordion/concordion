package org.concordion.internal;

import java.io.IOException;
import java.io.InputStream;

import nu.xom.Document;

import org.concordion.api.*;
import org.concordion.internal.util.IOUtil;

public class XMLSpecificationReader implements SpecificationReader {

    private final Source source;
    private final XMLParser xmlParser;
    private final DocumentParser documentParser;
    private SpecificationConverter specificationConverter;

    public XMLSpecificationReader(Source source, XMLParser xmlParser, DocumentParser documentParser) {
        this.source = source;
        this.xmlParser = xmlParser;
        this.documentParser = documentParser;
    }
    
    public Specification readSpecification(Resource resource) throws IOException {
        InputStream inputStream = asHtmlStream(resource);
        Document document;
        try {
            document = xmlParser.parse(inputStream, String.format("[%s: %s]", source, resource.getPath()));
        } catch (ParsingException e) {
            if (specificationConverter != null) {
                System.err.println("Error parsing generated HTML:\n" + IOUtil.readAsString(asHtmlStream(resource)));
            }
            throw e;
        }
        if (specificationConverter != null) {
            resource = new Resource(resource.getPath().replaceFirst("\\..*$", "\\.html"));
        }
        return documentParser.parse(document, resource);
    }

    private InputStream asHtmlStream(Resource resource) throws IOException {
        InputStream inputStream = source.createInputStream(resource);
        if (specificationConverter != null) {
            inputStream = specificationConverter.convert(resource, inputStream);
        }
        return inputStream;
    }

    @Override
    public boolean canFindSpecification(Resource resource) throws IOException {
        return source.canFind(resource);
    }

    @Override
    public void setSpecificationConverter(SpecificationConverter specificationConverter) {
        this.specificationConverter = specificationConverter;
    }
}
