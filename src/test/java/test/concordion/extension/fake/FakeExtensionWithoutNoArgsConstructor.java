package test.concordion.extension.fake;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;


public class FakeExtensionWithoutNoArgsConstructor extends FakeExtensionBase implements ConcordionExtension, DocumentParsingListener {

    private FakeExtensionWithoutNoArgsConstructor(String text) {
        super(text);
    }
    
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withDocumentParsingListener(this);
    }
}
