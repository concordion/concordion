package org.concordion.api.listener;

import org.concordion.api.Element;

public class RunSuccessEvent extends AbstractRunEvent {

    public RunSuccessEvent(Element element) {
        super(element);
    }

}
