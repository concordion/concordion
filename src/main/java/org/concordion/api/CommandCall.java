package org.concordion.api;

import org.concordion.internal.util.Check;

import java.util.*;

/**
 * Nested CommandCalls form an abstract syntax tree. (The XML is the concrete
 * syntax tree.)
 */
public class CommandCall {

    private final CommandCallList children = new CommandCallList();
    private final Command command;
    private CommandCall parent;
    private final String expression;
    private final Resource resource;
    private Element element;
    private Map<String, String> parameters = Collections.emptyMap();

    public CommandCall(CommandCall parent, Command command, Element element, String expression, Resource resource) {
        this.command = command;
        this.parent = parent;
        this.element = element;
        this.expression = expression;
        this.resource = resource;
    }

    public CommandCall getParent() {
        return this.parent;
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

    public String getParameter(String camelCaseParameterName, String spinalCaseParameterName) {
        String camelCaseParameter = parameters.get(camelCaseParameterName);
        if (camelCaseParameter != null) {
            return camelCaseParameter;
        }        String spinalCaseParameter = parameters.get(spinalCaseParameterName);
        if (spinalCaseParameter != null) {
            return spinalCaseParameter;
        }
        return null;
    }

    public void modifyTree(List<CommandCall> examples, List<CommandCall> beforeExamples) {
        this.getCommand().modifyCommandCallTree(this, examples, beforeExamples);
    }

    public void setParent(CommandCall parent) {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
        }

        this.parent = parent;

        if (parent != null) {
            parent.appendChild(this);
        }
    }
}
