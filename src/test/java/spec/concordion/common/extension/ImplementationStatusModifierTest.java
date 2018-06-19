package spec.concordion.common.extension;

import org.concordion.api.*;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ConcordionBuilder;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;

import java.util.ArrayList;
import java.util.List;

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

    private final List<String> beforeExampleCapturedNames = new ArrayList<String>();
    private final List<String> afterExampleCapturedNames = new ArrayList<String>();

    @BeforeExample
    public void saveNameBeforeExample(@ExampleName String name) {
        beforeExampleCapturedNames.add(name);
    }

    @AfterExample
    public void saveNameAfterExample(@ExampleName String name) {
        afterExampleCapturedNames.add(name);
    }

    public List<String> getBeforeExampleCapturedNames() {
        return beforeExampleCapturedNames;
    }

    public List<String> getAfterExampleCapturedNames() {
        return afterExampleCapturedNames;
    }


}
