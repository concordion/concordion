package org.concordion.api;

import org.concordion.internal.util.Check;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Nested CommandCalls form an abstract syntax tree. (The XML is the concrete
 * syntax tree.)
 */
public class CommandCall {

    private final CommandCallList children = new CommandCallList();
    private final Command command;
    private final String expression;
    private final Resource resource;
    private Element element;
    private Map<String, String> parameters = Collections.emptyMap();

    public CommandCall(Command command, Element element, String expression, Resource resource) {
        this.command = command;
        this.element = element;
        this.expression = expression;
        this.resource = resource;
    }
    
    public void setUp(Evaluator evaluator, ResultRecorder resultRecorder) {
        command.setUp(this, evaluator, resultRecorder);
    }

    public void execute(Evaluator evaluator, ResultRecorder resultRecorder) {
        command.execute(this, evaluator, resultRecorder);
    }

    public void verify(Evaluator evaluator, ResultRecorder resultRecorder) {
        command.verify(this, evaluator, resultRecorder);
    }

    public Result verifyInBackground(Evaluator evaluator, ResultRecorder resultRecorder) {
        return command.verifyInBackground(this, evaluator, resultRecorder);
    }

    public void appendChild(CommandCall commandNode) {
        children.append(commandNode);
    }

    public CommandCallList getChildren() {
        return children;
    }

    public Command getCommand() {
        return command;
    }

    public Element getElement() {
        return element;
    }

    public String getExpression() {
        return expression;
    }

    public Resource getResource() {
        return resource;
    }

    public boolean hasChildCommands() {
        return !children.isEmpty();
    }
    
    public void setElement(Element element) {
        Check.notNull(element, "element is null");
        this.element = element;
    }


    public void setParameters(Map<String, String> parameters) {
        this.parameters = new HashMap<String, String>(parameters);
    }

    public String getParameter(String parameter) {
        return parameters.get(parameter);
    }
}
