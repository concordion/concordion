package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.Attribute;
import org.concordion.internal.parser.support.ConcordionStatement;

import java.util.List;

public class ConcordionCommandNode extends Block {

    private final String expression;
    private final List<Attribute> attributes;
    private final String command;

    private ConcordionCommandNode(String command, String expression, List<Attribute> attributes, BasedSequence text) {
        super(text);
        this.command = command;
        this.expression = expression;
        this.attributes = attributes;
    }

    public static ConcordionCommandNode createNode(ConcordionStatement statement, BasedSequence text) {
        return new ConcordionCommandNode(statement.command.name, statement.command.value, statement.attributes, text);
    }

    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    public String getExpression() {
        return expression;
    }

    public String getCommand() {
        return command;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
