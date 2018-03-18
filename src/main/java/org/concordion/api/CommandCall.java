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
    private Map<String, Object> constantsForExecution = new HashMap();
    private boolean bypassExecution = false;

    public CommandCall(CommandCall parent, Command command, Element element, String expression, Resource resource) {
        this.command = command;
        this.parent = parent;
        this.element = element;
        this.expression = expression;
        this.resource = resource;
    }

    /*

    Sometimes during tree modification, we get command nodes 'left over' that are kept in the
    execution tree for consistancy, but can be skipped during execution. These are generally execute nodes
    on a table or a list where we copy the commands onto the other table or list nodes.

    Due to the way tree modification works, it's risky removing these from the execution tree
    completely - so we set a bypass flag on them. Also, removing them and moving their children up a level can cause
    ordering issues - the HTML might not be executed in order anymore.

     */
    public void setBypassExecution(boolean bypassExecution) {
        this.bypassExecution = bypassExecution;
    }

    public boolean bypassExecution() {
        return this.bypassExecution;
    }

    public void setConstantForExecution(String name, Object value) {
        constantsForExecution.put(name, value);
    }

    public Object getConstantForExecution(String levelVariable) {
        return constantsForExecution.get(levelVariable);
    }

    public CommandCall getParent() {
        return this.parent;
    }

    public void setUp(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {

        command.setUp(this, evaluator, resultRecorder, fixture);
    }

    public void execute(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        for (Map.Entry<String, Object> entry: constantsForExecution.entrySet()) {
            evaluator.setVariable(entry.getKey(), entry.getValue());
        }

        command.execute(this, evaluator, resultRecorder, fixture);
    }

    public void verify(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        command.verify(this, evaluator, resultRecorder, fixture);
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

    public void modifyTree(List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
        this.getCommand().modifyCommandCallTree(this, examples, beforeExamples);

        List<CommandCall> childrenCopy = new ArrayList(getChildren().asCollection());

        for (CommandCall childCall: childrenCopy) {
            childCall.modifyTree(examples, beforeExamples);
        }
    }

    public void transferToParent(CommandCall parent) {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
        }

        this.parent = parent;

        if (parent != null) {
            parent.appendChild(this);
        }
    }

}
