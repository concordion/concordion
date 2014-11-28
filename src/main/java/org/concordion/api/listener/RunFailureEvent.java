package org.concordion.api.listener;

import org.concordion.api.Element;

public class RunFailureEvent extends AbstractRunEvent {

    public RunFailureEvent(Element element) {
        super(element);
    }

}
