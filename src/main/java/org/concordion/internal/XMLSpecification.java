package org.concordion.internal;

import org.concordion.api.*;
import org.concordion.internal.command.ExampleCommand;
import org.concordion.internal.command.SpecificationCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XMLSpecification implements SpecificationByExample {

    private String testDescription;

    private final CommandCall rootCommandNode;
    private final SpecificationCommand specificationCommand;
    private final List<CommandCall> examples;
    private final List<CommandCall> beforeExamples;

    public XMLSpecification(CommandCall rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
        if (!(rootCommandNode.getCommand() instanceof SpecificationCommand)) {
            throw new IllegalStateException("Expected root command to be a SpecificationCommand");
        }
        specificationCommand = (SpecificationCommand) rootCommandNode.getCommand();
        specificationCommand.start(rootCommandNode);

        examples = new ArrayList<CommandCall>();
        beforeExamples = new ArrayList<CommandCall>();

        List<CommandCall> allExamples = findExamples(rootCommandNode);

        for (CommandCall call: allExamples) {
            if (call.getExpression().equalsIgnoreCase("before")) {
                beforeExamples.add(call);
            } else {
                examples.add(call);
            }
        }
    }

    public void processNode(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {

        if (hasNonExampleChildren(node)) {
            for (CommandCall before: beforeExamples) {
                SummarizingResultRecorder beforeResultRecorder = new SummarizingResultRecorder();
                before.getCommand().executeAsExample(before, evaluator, beforeResultRecorder);
                if (beforeResultRecorder.getTotalCount() > 0) {
                    beforeResultRecorder.setSpecificationDescription("Running before for example " + node.getExpression());
                    String errorText = String.format("Before example performed tests in %s %s",
                            testDescription,
                            beforeResultRecorder.printToString(null)
                    );
    
                    throw new ConcordionAssertionError(errorText, beforeResultRecorder);
                }
            }
        }
        
        if (node.getCommand().isExample()) {
            node.getCommand().executeAsExample(node, evaluator, resultRecorder);
        } else {
            node.execute(evaluator, resultRecorder);
        }
    }

    private boolean hasNonExampleChildren(CommandCall node) {
        Collection<CommandCall> children = node.getChildren().asCollection();
        for (CommandCall child : children) {
            if (!(child.getCommand().isExample())) {
                return true;
            }
        }
        return false;
    }

    public void process(Evaluator evaluator, ResultRecorder resultRecorder) {
        processNode(rootCommandNode, evaluator, resultRecorder);
    }

    public void setFixtureClass(Fixture fixture) {
        testDescription = fixture.getDescription();
    }

    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder) {
        if (testDescription.equals(example)) {
            processNode(rootCommandNode, evaluator, resultRecorder);
            return;
        }

        for (CommandCall commandCall: examples) {
            if (makeJunitTestName(commandCall).equals(example)) {
                resultRecorder.setForExample(true);
                processNode(commandCall, evaluator, resultRecorder);
            }
        }
    }

    public List<String> getExampleNames() {

        List<String> commands = new ArrayList<String>();

        for (CommandCall exampleCall: examples) {
            commands.add(makeJunitTestName(exampleCall));
        }

        // always add the main spec last. Helps with junit test ordering
        commands.add(testDescription);

        return commands;
    }

    private String makeJunitTestName(CommandCall exampleCall) {
        return exampleCall.getExpression();
    }

    private List<CommandCall> findExamples(CommandCall node) {

        List<CommandCall> commands = new ArrayList<CommandCall>();

        List<CommandCall> thisNodeCommands = node.getCommand().getExamples(node);
        for (CommandCall command: thisNodeCommands) {
            commands.add(command);
        }

        if (node.hasChildCommands()) {
            for (CommandCall child : node.getChildren().asCollection()) {
                commands.addAll(findExamples(child));
            }
        }

        return commands;
    }
    
    public void finish() {
        specificationCommand.finish(rootCommandNode);
    }
}
