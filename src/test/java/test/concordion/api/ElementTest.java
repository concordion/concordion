package test.concordion.api;

import java.io.StringReader;
import junit.framework.TestCase;
import nu.xom.Builder;
import org.concordion.api.Element;

public class ElementTest extends TestCase {

    public void testCanMoveChildrenToAnotherElement() throws Exception {
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
    
    public void testChildElementsCanBeFoundById() throws Exception {
        for (String namespaceDeclaration : new String[] { "", "xmlns='" + Element.XHTML_URI + "'" }) {
            
            String xhtml = "<html " + namespaceDeclaration + ">";
            xhtml += "<body>";
            xhtml += "<p id='first'>First paragraph</p>";
            xhtml += "<p id='second'>Second paragraph</p>";
            xhtml += "</body>";
            xhtml += "</html>";
            
            Element root = new Element(new Builder(false).build(new StringReader(xhtml)).getRootElement());
            
            assertEquals("First paragraph", root.getElementById("first").getText());
            assertEquals("Second paragraph", root.getElementById("second").getText());
            assertNull(root.getElementById("third"));
        }
    }
}
