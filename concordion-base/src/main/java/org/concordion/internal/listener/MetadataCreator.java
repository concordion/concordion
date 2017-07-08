package org.concordion.internal.listener;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.util.Check;

public class MetadataCreator implements DocumentParsingListener {

    /**
     * Adds Content-Type metadata to the document.
     */
    public void beforeParsing(Document document) {
        Element html = document.getRootElement();
        Element head = html.getFirstChildElement("head");
        Check.notNull(head, "<head> section is missing from document");
        if (!hasContentTypeMetadata(head)) {
            addContentTypeMetadata(head);
        }
    }

    private void addContentTypeMetadata(Element head) {
        Element meta = new Element("meta");
        meta.addAttribute(new Attribute("http-equiv", "content-type"));
        meta.addAttribute(new Attribute("content", "text/html; charset=UTF-8"));
        head.insertChild(meta, 0);
    }

    private boolean hasContentTypeMetadata(Element head) {
        Elements metaChildren = head.getChildElements("meta");
        for (int i=0; i < metaChildren.size(); i++) {
            Element metaChild = metaChildren.get(i);
            Attribute httpEquiv = metaChild.getAttribute("http-equiv");
            if (httpEquiv != null && "content-type".equalsIgnoreCase(httpEquiv.getValue())) {
                return true;
            }
        }
        return false;
    }
}
