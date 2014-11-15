package org.concordion.api.listener;

import org.concordion.api.Element;

public class AssertSuccessEvent extends AbstractElementEvent {

    public AssertSuccessEvent(Element element) {
        super(element);
    }

}