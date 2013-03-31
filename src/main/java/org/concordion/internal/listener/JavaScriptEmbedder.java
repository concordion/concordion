package org.concordion.internal.listener;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.util.Check;

public class JavaScriptEmbedder implements DocumentParsingListener {

    private final String javaScript;

    public JavaScriptEmbedder(String javaScript) {
        this.javaScript = javaScript;
    }
    
    public void beforeParsing(Document document) {
        Element html = document.getRootElement();
        Element head = html.getFirstChildElement("head");
        Check.notNull(head, "<head> section is missing from document");
        Element script = new Element("script");
        script.addAttribute(new Attribute("type", "text/javascript") );
        script.appendChild(javaScript);
        head.insertChild(script, 0);
    }
}
