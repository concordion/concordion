package org.concordion.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ValidityException;

public class XMLParser {

    public Document parse(InputStream inputStream) throws IOException, ParsingException {
        return parse(inputStream, null);
    }
    
    public Document parse(InputStream inputStream, String sourceDescription) throws IOException, ParsingException {
        Document document;
        Builder builder = new Builder();
        try {
            document = builder.build(inputStream);
        } catch (ValidityException e) {
            throw new ParsingException("Failed to validate XML document", e, sourceDescription);
        } catch (nu.xom.ParsingException e) {
            throw new ParsingException("Failed to parse XML document", e, sourceDescription);
        }
        return document;
    }

    public static Document parse(String xml) throws IOException, ParsingException {
        try {
            return new XMLParser().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (Throwable t) {
            throw new ParsingException("Failed to parse XML", t);
        }
    }

}
