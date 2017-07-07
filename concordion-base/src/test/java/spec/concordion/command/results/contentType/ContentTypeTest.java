package spec.concordion.command.results.contentType;

import nu.xom.Element;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ContentTypeTest {

    public String process(String html) {
        Element rootElement = new TestRig()
            .process(html)
            .getXOMDocument()
            .getRootElement();
        removeIrrelevantElements(rootElement);
        return rootElement.toXML();
    }

    private void removeIrrelevantElements(Element rootElement) {
        removeIrrelevantStylesheet(rootElement);
        removeIrrelevantFooter(rootElement);
    }

    private void removeIrrelevantStylesheet(Element rootElement) {
        Element head = rootElement.getFirstChildElement("head");
        Element style = head.getFirstChildElement("style");
        head.removeChild(style);
    }

    private void removeIrrelevantFooter(Element rootElement) {
        Element body = rootElement.getFirstChildElement("body");
        body.removeChild(rootElement.query("//div[@class='footer']").get(0));
    }
}
