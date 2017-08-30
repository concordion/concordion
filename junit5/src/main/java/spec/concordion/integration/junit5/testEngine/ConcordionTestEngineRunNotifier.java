package spec.concordion.integration.junit5.testEngine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class ConcordionTestEngineRunNotifier extends RunListener {
    private ConcordionTestDescriptor test;
    private EngineExecutionListener engineExecutionListener;

    public ConcordionTestEngineRunNotifier(ConcordionTestDescriptor test, EngineExecutionListener engineExecutionListener) {
        this.test = test;
        this.engineExecutionListener = engineExecutionListener;
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        engineExecutionListener.executionFinished(test, TestExecutionResult.successful());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        engineExecutionListener.executionFinished(test, TestExecutionResult.aborted(failure.getException()));
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
        engineExecutionListener.executionFinished(test, TestExecutionResult.failed(failure.getException()));
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        super.testIgnored(description);
        engineExecutionListener.executionFinished(test, TestExecutionResult.successful());
    }
}
