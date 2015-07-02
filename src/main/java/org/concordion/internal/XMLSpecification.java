package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.*;
import org.concordion.internal.command.ExampleCommand;
import org.concordion.internal.command.ThrowableCatchingDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XMLSpecification implements SpecificationByExample {

    private String testDescription;

    private final CommandCall rootCommandNode;
    private final List<CommandCall> examples;

    public XMLSpecification(CommandCall rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
        examples = findExamples(rootCommandNode);
    }

    public void processNode(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {
        if (node.getCommand().isExample()) {
            node.getCommand().executeAsExample(node, evaluator, resultRecorder);
        } else {
            node.execute(evaluator, resultRecorder);
        }
    }

    public void process(Evaluator evaluator, ResultRecorder resultRecorder) {
        processNode(rootCommandNode, evaluator, resultRecorder);
    }

    public void setFixtureClass(Class<?> fixture) {
        testDescription = Concordion.getDefaultFixtureClassName(fixture);
    }

    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder) {
        if (testDescription.equals(example)) {
            processNode(rootCommandNode, evaluator, resultRecorder);
            return;
        }

        for (CommandCall commandCall: examples) {
            if (commandCall.getExpression().equals(example)) {
                processNode(commandCall, evaluator, resultRecorder);
            }
        }
    }

    public List<String> getExampleNames() {

        List<String> commands = new ArrayList<String>();

        for (CommandCall exampleCall: examples) {
            commands.add(exampleCall.getExpression());
        }

        commands.add(testDescription);

        return commands;
    }

    private List<CommandCall> findExamples(CommandCall node) {

        List<CommandCall> commands = new ArrayList<CommandCall>();

        List<CommandCall> thisNodeCommands = node.getCommand().getExamples(node);
        for (CommandCall command: thisNodeCommands) {
            commands.add(command);
        }

        if (node.hasChildCommands()) {
            for (int i=0; i<node.getChildren().size(); i++) {
                commands.addAll(findExamples(node.getChildren().get(i)));
            }
        }

        return commands;
    }
}
