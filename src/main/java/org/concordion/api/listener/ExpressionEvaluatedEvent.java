package org.concordion.api.listener;

import org.concordion.api.Element;

public class ExpressionEvaluatedEvent {

    private final Element rowElement;

    public ExpressionEvaluatedEvent(Element rowElement) {
        this.rowElement = rowElement;
    }

    public Element getElement() {
        return rowElement;
    }

}
