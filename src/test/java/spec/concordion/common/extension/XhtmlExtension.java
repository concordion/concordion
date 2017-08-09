package spec.concordion.common.extension;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.ClassNameBasedSpecificationLocator;

public class XhtmlExtension implements ConcordionExtension {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender
            .withSpecificationLocator(new ClassNameBasedSpecificationLocator("xhtml"))
            .withTarget(new FileTargetWithSuffix("html"));
    }
    
}
