package org.concordion.api.listener;

import org.concordion.api.Target;

public class ConcordionBuildEvent {
    private final Target target;

    public ConcordionBuildEvent(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }
}