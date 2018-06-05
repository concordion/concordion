package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class ConcordionTableCell extends TableCell {
    private String command;
    private String expression;

    public ConcordionTableCell(BasedSequence chars, ConcordionCommandNode commandNode) {
        super(chars);
        this.command = commandNode.getCommand();
        this.expression = commandNode.getExpression();
    }

    public String getCommand() {
        return command;
    }

    public String getExpression() {
        return expression;
    }
}
