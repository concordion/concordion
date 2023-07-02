package spec.concordion.common.command.results.stylesheet;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.Element;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class StylesheetTest {

    private Element outputDocument;

    public void processDocument(String html) {
        outputDocument = new TestRig()
            .process(html)
            .getRootElement();
    }
    
    public String getRelativePosition(String outer, String target, String sibling) {
        Element outerElement = outputDocument.getFirstDescendantNamed(outer);
        
        int targetIndex = indexOfFirstChildWithName(outerElement, target);
        int siblingIndex = indexOfFirstChildWithName(outerElement, sibling);
        
        return targetIndex > siblingIndex ? "after" : "before";
    }
    
    private int indexOfFirstChildWithName(Element element, String name) {
        int index = 0;
        for (Element e : element.getChildElements()) {
            if (e.getLocalName().equals(name)) {
                return index;
            }
            index++;
        }
        throw new RuntimeException("No child <" + name + "> found.");
    }

    public boolean elementTextContains(String elementName, String s1, String s2) {
        Element element = outputDocument.getRootElement().getFirstDescendantNamed(elementName);
        String text = element.getText();
        return text.contains(s1) && text.contains(s2);
    }
}
