package test.concordion.extension.fake;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;


public class FakeExtension3 extends FakeExtensionBase implements ConcordionExtension, DocumentParsingListener {

    public FakeExtension3() {
        super();
    }
    
    public FakeExtension3(String text) {
        super(text);
    }
    
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withDocumentParsingListener(this);
    }
}
