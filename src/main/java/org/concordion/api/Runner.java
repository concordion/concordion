package org.concordion.api;

public interface Runner {
    
	ResultSummary execute(Resource resource, String href) throws Exception;
}
