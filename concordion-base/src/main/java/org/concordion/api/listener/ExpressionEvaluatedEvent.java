package org.concordion.api.listener;

import org.concordion.api.Element;

public class ExpressionEvaluatedEvent extends AbstractElementEvent {

    public ExpressionEvaluatedEvent(Element element) {
        super(element);
    }

}
