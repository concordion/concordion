package spec.concordion.integration.junit5.testEngine;

import org.concordion.Concordion;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.platform.engine.*;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

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

            ConcordionTestDescriptor ctd = (ConcordionTestDescriptor) test;
            try {
                RunNotifier runNotifier = new RunNotifier();

                runNotifier.addListener(new ConcordionTestEngineRunNotifier(ctd, request.getEngineExecutionListener()));

                System.err.println("Executing test: " + ctd.getClassUnderTest().getCanonicalName());
                new ConcordionRunner(ctd.getClassUnderTest()).run(runNotifier);

            } catch (InitializationError initializationError) {
                initializationError.printStackTrace();
                request.getEngineExecutionListener().executionFinished(test, TestExecutionResult.failed(initializationError));
            }

        });
    }
}
