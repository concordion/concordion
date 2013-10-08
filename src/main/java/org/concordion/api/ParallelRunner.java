package org.concordion.api;

import java.util.concurrent.Callable;

public interface ParallelRunner {
    
    Callable<RunnerResult> createRunnerTask(Resource resource, String href) throws Exception;
}
