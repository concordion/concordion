package org.concordion.api.listener;

import org.concordion.api.Element;

public class SurplusRowEvent {
    
    private final Element rowElement;

    public SurplusRowEvent(Element rowElement) {
        this.rowElement = rowElement;
    }

    public Element getRowElement() {
        return rowElement;
    }
}
