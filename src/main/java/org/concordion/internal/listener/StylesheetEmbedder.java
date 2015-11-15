package org.concordion.internal.listener;

import nu.xom.Document;
import nu.xom.Element;

import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.util.Check;

public class StylesheetEmbedder implements DocumentParsingListener {

    private final String stylesheetContent;
    private final boolean appendChild;

    public StylesheetEmbedder(String stylesheetContent) {
        this.stylesheetContent = stylesheetContent;
        this.appendChild = false;
    }
    
    public StylesheetEmbedder(String stylesheetContent, boolean appendChild) {
        this.stylesheetContent = stylesheetContent;
        this.appendChild = appendChild;
    }
    
    public void beforeParsing(Document document) {
        Element html = document.getRootElement();
        Element head = html.getFirstChildElement("head");
        Check.notNull(head, "<head> section is missing from document");
        Element style = new Element("style");

        String updatedStylesheetContent = updateConcordionNamespacePrefix(html, stylesheetContent);
        style.appendChild(updatedStylesheetContent);

        if (appendChild) {
        	head.appendChild(style);
        } else {
        	head.insertChild(style, 0);
        }
    }

    /**
     * If the prefix for the Concordion namespace is something other than "concordion", eg "c", this 
     * updates the stylesheet to use the prefix.
     * This is required since <a href="http://stackoverflow.com/questions/24628932/do-css-namespace-attribute-selectors-work-with-xhtml-html-elements">
     * namespaced CSS selectors generally don't work with HTML</a>, so <code>embedded.css</code> uses hardcoded namespace prefixes (eg. <code>[concordion\:example]</code>).
     */
    private String updateConcordionNamespacePrefix(Element html, String stylesheetContent) {
        for (int i=0; i<html.getNamespaceDeclarationCount(); i++) {
            String prefix = html.getNamespacePrefix(i);
            System.out.println(prefix);
            System.out.println(html.getNamespaceURI(prefix));
            if (ConcordionBuilder.NAMESPACE_CONCORDION_2007.equals(html.getNamespaceURI(prefix))) {
                return stylesheetContent.replace("concordion\\:", prefix + "\\:");
            }
        }
        return stylesheetContent;
    }
}
