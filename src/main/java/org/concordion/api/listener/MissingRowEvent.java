package org.concordion.api.listener;

import org.concordion.api.Element;

public class MissingRowEvent {
    
    private final Element rowElement;

    public MissingRowEvent(Element rowElement) {
        this.rowElement = rowElement;
    }

    public Element getRowElement() {
        return rowElement;
    }
}
