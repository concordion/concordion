package test.concordion.internal.listener;

import nu.xom.Document;
import nu.xom.Element;

import org.concordion.internal.listener.DocumentStructureImprover;

import junit.framework.TestCase;

public class DocumentStructureImproverTest extends TestCase {

    private DocumentStructureImprover improver = new DocumentStructureImprover();
    private Element html = new Element("html");
    private Document document = new Document(html);
    
    public void testAddsHeadIfMissing() throws Exception {
        improver.beforeParsing(document);
        assertEquals("<html><head /></html>", html.toXML());

        // Check it does not add it again if we repeat the call
        improver.beforeParsing(document);
        assertEquals("<html><head /></html>", html.toXML());
    }

    public void testTransfersEverythingBeforeBodyIntoNewlyCreatedHead() throws Exception {
        Element style1 = new Element("style1");
        Element style2 = new Element("style2");
        html.appendChild(style1);
        html.appendChild(style2);

        Element body = new Element("body");
        body.appendChild("some ");
        Element bold = new Element("b");
        bold.appendChild("bold text");
        body.appendChild(bold);
        html.appendChild(body);
        improver.beforeParsing(document);
        
        assertEquals("<html><head><style1 /><style2 /></head><body>some <b>bold text</b></body></html>", html.toXML());
    }

}
