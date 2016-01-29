package org.concordion.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import nu.xom.Document;

import org.concordion.api.*;

public class XMLSpecificationReader implements SpecificationReader {

    private final Source source;
    private final XMLParser xmlParser;
    private final DocumentParser documentParser;
    private SpecificationConverter specificationConverter;
    private Target copySourceHtmlTarget;

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
                System.err.println("Error parsing generated HTML:\n" + source.readAsString(asHtmlStream(resource)));
            }
            throw e;
        }
        if (specificationConverter != null) {
            resource = new Resource(resource.getPath().replaceFirst("\\..*$", "\\.html"));
        }
        return documentParser.parse(document, resource);
    }

    @Override
    public boolean canFindSpecification(Resource resource) throws IOException {
        return source.canFind(resource);
    }

    @Override
    public void setSpecificationConverter(SpecificationConverter specificationConverter) {
        this.specificationConverter = specificationConverter;
    }

    @Override
    public void setCopySourceHtmlTarget(Target target) {
        this.copySourceHtmlTarget = target;
    }
    
    private InputStream asHtmlStream(Resource resource) throws IOException {
        InputStream inputStream = source.createInputStream(resource);
        if (specificationConverter != null) {
            inputStream = specificationConverter.convert(inputStream);
        }
        if (copySourceHtmlTarget != null) {
            inputStream = copySourceHtml(resource, inputStream);
        }
        
        return inputStream;
    }

    private String asString(InputStream inputStream) {
        String markdown;
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputStream, "UTF-8");
            markdown = scanner.useDelimiter("\\A").next();
        } finally {
            scanner.close();
        }
        return markdown;
    }
    
    private InputStream copySourceHtml(Resource resource, InputStream inputStream) throws IOException {
        Resource sourceHtmlResource = new Resource(resource.getPath() + ".html");
        System.out.println(String.format("[Source: %s]", copySourceHtmlTarget.resolvedPathFor(sourceHtmlResource)));
        String html = asString(inputStream);
        copySourceHtmlTarget.write(sourceHtmlResource, html);
        inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        return inputStream;
    }
}
