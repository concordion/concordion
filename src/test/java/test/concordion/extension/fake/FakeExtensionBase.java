package test.concordion.extension.fake;

import org.concordion.api.Element;

import nu.xom.Document;

public abstract class FakeExtensionBase {

    public static final String FAKE_EXTENSION_ATTR_NAME = "fake.extensions";
    private final String text;

    public FakeExtensionBase() {
        text = this.getClass().getSimpleName();
    }
    
    public FakeExtensionBase(String text) {
        this.text = text;
    }

    public void beforeParsing(Document document) {
        Element rootElement = new Element(document.getRootElement());
        String newValue = text;
        String existingValue = rootElement.getAttributeValue(FAKE_EXTENSION_ATTR_NAME);
        if (existingValue != null) {
            newValue = existingValue + ", " + newValue;
        }
        rootElement.addAttribute(FAKE_EXTENSION_ATTR_NAME, newValue);
    }
}
