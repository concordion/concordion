package spec.concordion.common.extension;

import org.concordion.api.Element;
import org.concordion.api.ImplementationStatus;
import org.concordion.api.ImplementationStatusModifier;
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
public class ImplementationStatusModifierTest extends AbstractExtensionTestCase {

    public void addExtension() {
        setExtension(new ConcordionExtension() {
            @Override
            public void addTo(ConcordionExtender concordionExtender) {
                concordionExtender.withImplementationStatusModifier(new ImplementationStatusModifier() {
                    @Override
                    public ImplementationStatus getStatusForExample(Element exampleElement) {
                        String exampleName = exampleElement.getAttributeValue("example", ConcordionBuilder.NAMESPACE_CONCORDION_2007);
                        if (exampleName.endsWith("Ignored")) {
                            return ImplementationStatus.IGNORED;
                        } else {
                            return null;
                        }
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
