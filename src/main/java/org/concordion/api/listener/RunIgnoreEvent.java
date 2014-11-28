package org.concordion.api.listener;

import org.concordion.api.Element;

public class RunIgnoreEvent extends AbstractRunEvent {

    public RunIgnoreEvent(Element element) {
        super(element);
    }

}
