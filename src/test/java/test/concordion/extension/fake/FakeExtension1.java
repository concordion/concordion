package test.concordion.extension.fake;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;


public class FakeExtension1 extends FakeExtensionBase implements ConcordionExtension, DocumentParsingListener {

    public FakeExtension1() {
        super();
    }
    
    public FakeExtension1(String text) {
        super(text);
    }
    
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withDocumentParsingListener(this);
    }
}
