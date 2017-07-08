package org.concordion.api.listener;

import org.concordion.api.Element;

public class AssertFailureEvent extends AbstractElementEvent {

    protected final String expected;
    protected final Object actual;

    public AssertFailureEvent(Element element, String expected, Object actual) {
        super(element);
        this.expected = expected;
        this.actual = actual;
    }

    public String getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }

}