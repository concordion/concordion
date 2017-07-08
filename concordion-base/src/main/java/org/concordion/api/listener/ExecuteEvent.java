package org.concordion.api.listener;

import org.concordion.api.Element;

public class ExecuteEvent extends AbstractElementEvent {

    public ExecuteEvent(Element element) {
        super(element);
    }

}
