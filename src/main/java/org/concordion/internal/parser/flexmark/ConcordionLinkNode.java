package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.LinkNode;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class ConcordionLinkNode extends LinkNode {

    private String runner;
    private String command;

    // TODO check what this does
    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        astExtraChars(out);
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
