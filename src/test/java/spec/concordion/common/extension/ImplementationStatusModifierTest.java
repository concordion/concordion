package spec.concordion.common.extension;

import org.concordion.api.*;
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
                    public ImplementationStatus getStatusForExample(ExampleDefinition exampleDefinition) {
                        if (exampleDefinition.getName().endsWith("Ignored")) {
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
