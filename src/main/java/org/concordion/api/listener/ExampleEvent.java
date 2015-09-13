package org.concordion.api.listener;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.ResultRecorder;

public class ExampleEvent {

    private final CommandCall node;
    private final ResultRecorder resultRecorder;

    public ExampleEvent(CommandCall node, ResultRecorder resultRecorder) {
        this.node = node;
        this.resultRecorder = resultRecorder;
    }

    public CommandCall getNode() {
        return node;
    }
    
    public ResultRecorder getResultRecorder() {
        return resultRecorder;
    }
}
