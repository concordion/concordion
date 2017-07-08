package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.ResultSummary;

/**
 * @since 2.0.2
 */
public class OuterExampleEvent {

    private final Element element;
    private final ResultSummary resultSummary;
    private final String exampleName;

    public OuterExampleEvent(String exampleName, Element element, ResultSummary resultSummary) {
        this.exampleName = exampleName;
        this.resultSummary = resultSummary;
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public ResultSummary getResultSummary() {
        return resultSummary;
    }

    public String getExampleName() {
        return exampleName;
    }
}
