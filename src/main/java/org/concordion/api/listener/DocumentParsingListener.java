package org.concordion.api.listener;

import java.util.EventListener;

import nu.xom.Document;

public interface DocumentParsingListener extends EventListener {

    void beforeParsing(Document document);
}
