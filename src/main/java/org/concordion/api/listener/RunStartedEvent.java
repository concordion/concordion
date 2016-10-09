package org.concordion.api.listener;

import org.concordion.api.Element;

public class RunStartedEvent {
    private final Element element;
    private final String href;

    public RunStartedEvent(Element element, String href) {
        this.element = element;
        this.href = href;
    }

    public Element getElement() {
        return element;
    }

    public String getHref() {
        return href;
    }
}
