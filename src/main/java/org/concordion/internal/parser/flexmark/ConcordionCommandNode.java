package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.CharSubSequence;
import org.concordion.internal.parser.markdown.ConcordionMarkdownException;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.concordion.internal.parser.support.ConcordionStatement;

public class ConcordionCommandNode extends Node {

    private final BasedSequence expression;
    private final String command;

    protected ConcordionCommandNode(String command, BasedSequence expression, BasedSequence text) {
        super(text);
        this.command = command;
        this.expression = expression;
    }

    public static ConcordionCommandNode createNode(ConcordionStatement statement, BasedSequence expression, BasedSequence text) {
        if (expression.equals("c:run")) {
            throw new ConcordionMarkdownException("Markdown link contains invalid URL for \"c:run\" command.\n"
                    + "Set the URL to the location of the specification to be run, rather than '-'.\n"
                    + "For example, [My Specification](mySpec.md \"c:run\")");
        }
        System.out.println(statement.command.name);
        System.out.println(statement.command.value);
        return new ConcordionCommandNode(statement.command.name, CharSubSequence.of(statement.command.value), text);
    }

    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        astExtraChars(out);
    }

    public BasedSequence getExpression() {
        return expression;
    }

    public String getCommand() {
        return command;
    }
}
