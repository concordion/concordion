package test.concordion.extension.fake;

import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;

public class FakeExtension2Factory implements ConcordionExtensionFactory {

    public FakeExtension2Factory() {
        super();
    }
    
    public ConcordionExtension createExtension() {
        return new FakeExtension2("FakeExtension2FromFactory");
    }
}
