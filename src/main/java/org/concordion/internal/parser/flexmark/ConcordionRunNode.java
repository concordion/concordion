package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.LinkNode;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class ConcordionRunNode extends LinkNode {

    private String runner;
    private String command;

    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    public String getRunner() {
        return runner;
    }

    public void setRunner(String runner) {
        this.runner = runner;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
