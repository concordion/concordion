package org.concordion.api.listener;

import org.concordion.api.Element;

public abstract class AbstractRunEvent extends AbstractElementEvent{

    public AbstractRunEvent(Element element) {
        super(element);
    }

}
