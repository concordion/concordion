package org.concordion.integration.junit.platform.engine;

import org.concordion.internal.cache.RunResultsCache;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;

public class ConcordionEngineExecutionContext implements EngineExecutionContext {

    private final ExecutionRequest request;

    private final RunResultsCache runResultsCache = RunResultsCache.SINGLETON;

    public ConcordionEngineExecutionContext(ExecutionRequest request) {
        super();
        this.request = request;
    }

    public RunResultsCache getRunResultsCache() {
        return runResultsCache;
    }

    public TestDescriptor getRootTestDescriptor() {
        return request.getRootTestDescriptor();
    }

    public EngineExecutionListener getEngineExecutionListener() {
        return request.getEngineExecutionListener();
    }

    public ConfigurationParameters getConfigurationParameters() {
        return request.getConfigurationParameters();
    }

}
