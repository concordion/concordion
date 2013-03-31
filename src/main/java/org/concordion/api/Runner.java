package org.concordion.api;

public interface Runner {
    
    RunnerResult execute(Resource resource, String href) throws Exception;
}
