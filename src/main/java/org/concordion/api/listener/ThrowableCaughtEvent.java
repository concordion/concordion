package org.concordion.api.listener;

import org.concordion.api.Element;

public class ThrowableCaughtEvent {

    private final Throwable throwable;
    private final Element element;
    private final String expression;

    public ThrowableCaughtEvent(Throwable throwable, Element element, String expression) {
        this.element = element;
        this.throwable = throwable;
        this.expression = expression;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Element getElement() {
        return element;
    }

    public String getExpression() {
        return expression;
    }
}
