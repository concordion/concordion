package test.concordion.internal.listener;

import junit.framework.TestCase;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.concordion.internal.listener.MetadataCreator;

public class MetadataCreatorTest extends TestCase {

    private MetadataCreator metadataCreator = new MetadataCreator();
    private Element html = new Element("html"); 
    private Document document = new Document(html);
    private Element head = new Element("head");
    
    public MetadataCreatorTest() {
        html.appendChild(head);
    }
    
    public void testAddsContentTypeMetadataIfMissing() throws Exception {
        metadataCreator.beforeParsing(document);
        assertEquals("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /></head></html>", html.toXML());
    }

    public void testDoesNotAddContentTypeMetadataIfAlreadyPresent() throws Exception {
        Element meta = new Element("meta");
        meta.addAttribute(new Attribute("http-equiv", "Content-Type"));
        meta.addAttribute(new Attribute("content", "text/html; charset=UTF-8"));
        head.appendChild(new Element(meta));
        assertEquals("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head></html>", html.toXML());
        metadataCreator.beforeParsing(document);
        assertEquals("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head></html>", html.toXML());
    }
}
