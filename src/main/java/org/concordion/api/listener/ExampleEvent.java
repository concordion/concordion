package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.ResultSummary;

public class ExampleEvent {

    private final Element element;
    private final ResultSummary resultSummary;

    public ExampleEvent(Element element, ResultSummary resultSummary) {
        this.resultSummary = resultSummary;
        this.element = element;
    }
    
    public Element getElement() {
        return element;
    }

    public ResultSummary getResultSummary() {
        return resultSummary;
    }
}
