package org.concordion.api.listener;

import org.concordion.api.Element;

public class AssertSuccessEvent {

    protected final Element element;

    public AssertSuccessEvent(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

}