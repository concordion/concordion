package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

public class RunIgnoreEvent extends RunEvent {
	
    public RunIgnoreEvent(Element element) {
    	super(element, Result.IGNORED);
    }

    public RunIgnoreEvent(Element element, ResultSummary resultSummary) {
    	super(element, resultSummary);
     }
}
