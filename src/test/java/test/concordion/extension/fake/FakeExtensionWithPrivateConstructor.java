package test.concordion.extension.fake;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;


public class FakeExtensionWithPrivateConstructor extends FakeExtensionBase implements ConcordionExtension, DocumentParsingListener {

    private FakeExtensionWithPrivateConstructor() {
        super();
    }
    
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withDocumentParsingListener(this);
    }
}
