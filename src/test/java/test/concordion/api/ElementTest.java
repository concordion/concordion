package test.concordion.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Document;

import org.concordion.api.Element;
import org.concordion.internal.XMLParser;
import org.junit.Test;

public class ElementTest {

    @Test
    public void canMoveChildrenToAnotherElement() throws Exception {
        Element fred = new Element("fred");
        fred.appendChild(new Element("child1"));
        Element child2 = new Element("child2");
        fred.appendChild(child2);
        child2.appendChild(new Element("grandchild"));
        assertEquals("<fred><child1 /><child2><grandchild /></child2></fred>", fred.toXML());
        
        Element barney = new Element("barney");
        fred.moveChildrenTo(barney);
        assertEquals("<fred />", fred.toXML());
        assertEquals("<barney><child1 /><child2><grandchild /></child2></barney>", barney.toXML());
    }
    
    @Test
    public void childElementsCanBeFoundById() throws Exception {
        for (String namespaceDeclaration : new String[] { "", "xmlns='" + Element.XHTML_URI + "'" }) {
            
            String xhtml = "<html " + namespaceDeclaration + ">";
            xhtml += "<body>";
            xhtml += "<p id='first'>First paragraph</p>";
            xhtml += "<p id='second'>Second paragraph</p>";
            xhtml += "</body>";
            xhtml += "</html>";
            
            Element root = new Element(new Builder(false).build(new StringReader(xhtml)).getRootElement());
            
            assertNotNull(root.getElementById("first"));
            assertEquals("First paragraph", root.getElementById("first").getText());
            assertNotNull(root.getElementById("second"));
            assertEquals("Second paragraph", root.getElementById("second").getText());
            assertNull(root.getElementById("third"));
        }
    }
    
    @Test
    public void getParentElementOfChildReturnsParent() throws Exception {
        Document document = XMLParser.parse("<root><child1/><child2><grandChild/>x</child2></root>");
        Element root = new Element(document.getRootElement());
        Element child1 = (root.getChildElements("child1"))[0];
        Element child2 = (root.getChildElements("child2"))[0];
        Element grandChild = (child2.getChildElements("grandChild"))[0];
        assertEquals(root, child1.getParentElement());
        assertEquals(root, child2.getParentElement());
        assertEquals(child2, grandChild.getParentElement());
    }
    
    @Test
    public void getParentElementOfRootReturnsNull() throws Exception {
        Document document = XMLParser.parse("<root></root>");
        Element root = new Element(document.getRootElement());
        assertNull(root.getParentElement());
    }

    @Test
    public void getParentElementOfOrphanReturnsNull() {
        Element fred = new Element("fred");
        assertEquals(null, fred.getParentElement());
    }
}
