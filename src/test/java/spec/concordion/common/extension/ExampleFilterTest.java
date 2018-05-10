package spec.concordion.common.extension;

import org.concordion.api.Element;
import org.concordion.api.ExampleFilter;
import org.concordion.api.FullOGNL;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ConcordionBuilder;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;

/**
 * Created by bondocaadmin on 10/05/2018.
 */
@RunWith(ConcordionRunner.class)
@FullOGNL
public class ExampleFilterTest extends AbstractExtensionTestCase {

    public void addExampleFilterExtension() {
        setExtension(new ConcordionExtension() {
            @Override
            public void addTo(ConcordionExtender concordionExtender) {
                concordionExtender.withExampleFilter(new ExampleFilter() {
                    @Override
                    public boolean shouldSkip(Element exampleElement) {
                        String exampleName = exampleElement.getAttributeValue("example", ConcordionBuilder.NAMESPACE_CONCORDION_2007);
                        return exampleName.startsWith("skipped");
                    }
                });
            }
        });
    }

    @Override
    public ProcessingResult getProcessingResult() {
        return super.getProcessingResult();
    }
}
