package org.concordion.internal.listener;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.concordion.api.Resource;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.util.Check;

public class JavaScriptLinker implements DocumentParsingListener, SpecificationProcessingListener {

    private final Resource javaScriptResource;
    private Element script;

    public JavaScriptLinker(Resource javaScriptResource) {
        this.javaScriptResource = javaScriptResource;
    }

    public void beforeParsing(Document document) {
        nu.xom.Element html = document.getRootElement();
        nu.xom.Element head = html.getFirstChildElement("head");
        Check.notNull(head, "<head> section is missing from document");
        script = new nu.xom.Element("script");
        script.addAttribute(new Attribute("type", "text/javascript"));
        
        // Fix for Issue #26: Strict XHTML DTD requires an explicit end tag for <script> element
        // Thanks to Matthias Schwegler for reporting and supplying a fix for this.
        script.appendChild("");
        
        head.appendChild(script);
    }

    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        Resource resource = event.getResource();
        
        //work around with setValue() necessary for Concordion.NET
        Attribute srcAttribute = new Attribute("src", "");
        srcAttribute.setValue(resource.getRelativePath(javaScriptResource));
        script.addAttribute(srcAttribute);
    }

    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
    }

}
