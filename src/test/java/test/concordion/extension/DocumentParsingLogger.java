package test.concordion.extension;

import java.io.PrintStream;

import nu.xom.Document;

import org.concordion.api.listener.*;

public class DocumentParsingLogger implements DocumentParsingListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void beforeParsing(Document document) {
        stream.println("Before Parsing Document");
    }
}