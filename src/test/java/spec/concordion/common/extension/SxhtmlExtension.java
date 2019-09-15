package spec.concordion.common.extension;

import org.concordion.api.SpecificationConverter;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.ClassNameBasedSpecificationLocator;

import java.io.IOException;
import java.io.InputStream;

public class SxhtmlExtension implements ConcordionExtension {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender
            .withSpecificationType("sxhtml", new SpecificationConverter() {
                @Override
                public InputStream convert(InputStream inputStream, String specificationName) throws IOException {
                    return inputStream;
                }
            });
    }
    
}
