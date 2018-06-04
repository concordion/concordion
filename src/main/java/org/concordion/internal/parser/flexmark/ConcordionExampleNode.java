package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.Block;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.Attribute;
import org.concordion.internal.parser.support.ConcordionStatement;

import java.util.List;

public class ConcordionExampleNode extends Block {
    private final String command;
    private final String expression;
    private final List<Attribute> attributes;

    private ConcordionExampleNode(String command, String expression, List<Attribute> attributes) {
        super(BasedSequence.NULL);
        this.command = command;
        this.expression = expression;
        this.attributes = attributes;
    }
//        super(command, expression, attributes, BasedSequence.NULL);

    public static ConcordionExampleNode createNode(ConcordionStatement statement) {
        return new ConcordionExampleNode(statement.command.name, statement.command.value, statement.attributes);
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