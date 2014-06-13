package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

public class RunSuccessEvent extends RunEvent {

	public RunSuccessEvent(Element element) {
        super(element,Result.SUCCESS);
    }
 
    public RunSuccessEvent(Element element, ResultSummary resultSummary) {
        super(element,resultSummary);
    }
}
