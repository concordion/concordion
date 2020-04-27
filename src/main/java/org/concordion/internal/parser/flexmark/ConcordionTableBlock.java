package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.Attribute;

import java.util.List;

public class ConcordionTableBlock extends TableBlock {
    private final List<Attribute> attributes;
    private String command;
    private String expression;

    public ConcordionTableBlock(BasedSequence chars, ConcordionCommandNode commandNode) {
        this.command = commandNode.getCommand();
        this.expression = commandNode.getExpression();
        this.attributes = commandNode.getAttributes();
    }

    public String getCommand() {
        return command;
    }

    public String getExpression() {
        return expression;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
