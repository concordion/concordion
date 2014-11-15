package org.concordion.api.listener;

import org.concordion.api.Element;

public class ThrowableCaughtEvent extends AbstractElementEvent {

    private final Throwable throwable;
    private final String expression;

    public ThrowableCaughtEvent(Throwable throwable, Element element, String expression) {
        super(element);
        this.throwable = throwable;
        this.expression = expression;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getExpression() {
        return expression;
    }
}
