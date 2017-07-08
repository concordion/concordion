package org.concordion.internal.listener;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.util.Check;

public class DocumentStructureImprover implements DocumentParsingListener {

    /**
     * Improves the structure of an HTML document. If the &lt;head&gt; section 
     * is missing, one is added at the top of the document and any nodes
     * in front of the &lt;body&gt; section are moved into it.
     */
    public void beforeParsing(Document document) {
        Element html = document.getRootElement();
        Check.isTrue("html".equals(html.getLocalName()),
                "Only <html> documents are supported (<" + html.getLocalName() + "> is not)");
        
        if (!hasHeadSection(html)) {
            Element head = new Element("head");
            copyNodesBeforeBodyIntoHead(html, head);
            html.insertChild(head, 0);
        }
    }

    private void copyNodesBeforeBodyIntoHead(Element html, Element head) {
        for (Node child : nodesBeforeBody(html)) {
            child.detach();
            head.appendChild(child);
        }
    }

    private boolean hasHeadSection(Element html) {
        return html.getFirstChildElement("head") != null;
    }

    private List<Node> nodesBeforeBody(Element html) {
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < html.getChildCount(); i++) {
            Node child = html.getChild(i);
            if (isBodySection(child)) {
                break;
            }
            nodes.add(child);
        }
        return nodes;
    }

    private boolean isBodySection(Node child) {
        return (child instanceof Element) && 
            ((Element) child).getLocalName().equals("body");
    }
}
