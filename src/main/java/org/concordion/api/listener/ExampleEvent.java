package org.concordion.api.listener;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;

public class ExampleEvent {

    private final CommandCall node;

    public ExampleEvent(CommandCall node) {
        this.node = node;
    }

    public CommandCall getNode() {
        return node;
    }
}
