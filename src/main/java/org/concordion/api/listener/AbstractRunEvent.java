package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.SingleResultSummary;

public abstract class AbstractRunEvent {

	private final ResultSummary resultSummary;
	private final Element element;
	
	public AbstractRunEvent(Element element, ResultSummary summary) {
		this.resultSummary= summary;
		this.element = element;
	}
	
	public AbstractRunEvent(Element element, Result result) {
		this.resultSummary = new SingleResultSummary(result);
		this.element = element;
	}

	public ResultSummary getResultSummary() {
		return resultSummary;
	}

	public Element getElement() {
		return element;
	}
	
}
