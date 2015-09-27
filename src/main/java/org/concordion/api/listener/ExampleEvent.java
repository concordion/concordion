package org.concordion.api.listener;

import org.concordion.api.CommandCall;
import org.concordion.api.ResultSummary;

public class ExampleEvent {

    private final CommandCall node;
    private final ResultSummary resultSummary;

    public ExampleEvent(CommandCall node, ResultSummary resultSummary) {
        this.node = node;
        this.resultSummary = resultSummary;
    }

    public CommandCall getNode() {
        return node;
    }
    
    public ResultSummary getResultSummary() {
        return resultSummary;
    }
}
