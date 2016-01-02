package spec.concordion.extension;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.ClassNameBasedSpecificationLocator;
import org.concordion.internal.FileTargetWithSuffix;
import org.concordion.internal.util.IOUtil;

public class XhtmlExtension implements ConcordionExtension {

    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender
            .withSpecificationLocator(new ClassNameBasedSpecificationLocator("xhtml"))
            .withTarget(new FileTargetWithSuffix("html", new IOUtil()));
    }
    
}
