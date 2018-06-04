package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.support.Attribute;
import org.concordion.internal.parser.support.ConcordionStatement;

import java.util.List;

public class ConcordionExampleNode extends ConcordionCommandNode {
    protected ConcordionExampleNode(String command, String expression, List<Attribute> attributes) {
        super(command, expression, attributes, BasedSequence.NULL);
    }

    public static ConcordionExampleNode createNode(ConcordionStatement statement) {
        return new ConcordionExampleNode(statement.command.name, statement.command.value, statement.attributes);
    }
}