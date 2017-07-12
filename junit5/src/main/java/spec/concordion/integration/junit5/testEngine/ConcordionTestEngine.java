package spec.concordion.integration.junit5.testEngine;

import org.concordion.Concordion;
import org.junit.platform.engine.*;

import java.util.Optional;

/**
 * Created by tim on 12/07/17.
 */
public class ConcordionTestEngine implements TestEngine {
    private static final String ID = ConcordionTestEngine.class.getCanonicalName();

    public ConcordionTestEngine() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        return new ConcordionEngineDescriptor(uniqueId, getId(), discoveryRequest);
    }

    @Override
    public void execute(ExecutionRequest request) {
        ConcordionEngineDescriptor descriptor = (ConcordionEngineDescriptor) request.getRootTestDescriptor();
        descriptor.getChildren().forEach(test -> {
            request.getEngineExecutionListener().dynamicTestRegistered(test);
        });
        descriptor.getChildren().forEach(test -> {
            request.getEngineExecutionListener().executionStarted(test);
            request.getEngineExecutionListener().executionFinished(test, TestExecutionResult.successful());
        });
    }
}
