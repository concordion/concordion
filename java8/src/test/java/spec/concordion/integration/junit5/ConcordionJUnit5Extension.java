package spec.concordion.integration.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.extension.ExtensionContext.*;

/**
 * Created by tim on 4/07/17.
 */
public class ConcordionJUnit5Extension implements BeforeTestExecutionCallback, TestInstancePostProcessor {
    private static final Namespace NAMESPACE =
            Namespace.create("org", "concordion", "ConcordionJUnit5Extension");

    @Override
    public void beforeTestExecution(TestExtensionContext context) throws Exception {
        System.err.println("test execute");
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        System.err.println("post process");
    }
}
