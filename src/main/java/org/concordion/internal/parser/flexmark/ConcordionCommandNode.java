package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.Block;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.concordion.internal.parser.markdown.ConcordionMarkdownException;
import org.concordion.internal.parser.support.Attribute;
import org.concordion.internal.parser.support.ConcordionStatement;

import java.util.List;

public class ConcordionCommandNode extends Block {

    private final String expression;
    private final List<Attribute> attributes;
    private final String command;

    protected ConcordionCommandNode(String command, String expression, List<Attribute> attributes, BasedSequence text) {
        super(text);
        this.command = command;
        this.expression = expression;
        this.attributes = attributes;
    }

    public static ConcordionCommandNode createNode(ConcordionStatement statement, BasedSequence expression, BasedSequence text) {
        // TODO move next statement
        if (expression.equals("c:run")) {
            throw new ConcordionMarkdownException("Markdown link contains invalid URL for \"c:run\" command.\n"
                    + "Set the URL to the location of the specification to be run, rather than '-'.\n"
                    + "For example, [My Specification](mySpec.md \"c:run\")");
        }
        return new ConcordionCommandNode(statement.command.name, statement.command.value, statement.attributes, text);
    }

    public static ConcordionCommandNode createNode(String command, ConcordionStatement statement, BasedSequence expression, BasedSequence text) {
        return new ConcordionCommandNode(command, statement.command.value, statement.attributes, text);
    }

    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        astExtraChars(out);
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
