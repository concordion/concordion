package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

public class RunFailureEvent extends RunEvent {
	
    public RunFailureEvent(Element element) {
        super(element,Result.FAILURE);
    }
 
    public RunFailureEvent(Element element, ResultSummary resultSummary) {
        super(element,resultSummary);
    }
}
