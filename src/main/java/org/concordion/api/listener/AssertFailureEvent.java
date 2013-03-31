package org.concordion.api.listener;

import org.concordion.api.Element;

public class AssertFailureEvent {

    protected final Element element;
    protected final String expected;
    protected final Object actual;

    public AssertFailureEvent(Element element, String expected, Object actual) {
        this.element = element;
        this.expected = expected;
        this.actual = actual;
    }

    public Element getElement() {
        return element;
    }

    public String getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }

}