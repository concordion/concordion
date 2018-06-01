package org.concordion.internal;

import org.concordion.api.*;
import org.concordion.internal.command.SpecificationCommand;
import org.concordion.internal.util.SimpleFormatter;

import java.util.ArrayList;
import java.util.List;

public class XMLSpecification implements SpecificationByExample {

    public static final String OUTER_EXAMPLE_NAME = "[Outer]";
    public static final String OUTER_EXAMPLE_SUFFIX = " " + OUTER_EXAMPLE_NAME;

    private String testDescription;
    private final CommandCall rootCommandNode;

    private final SpecificationCommand specificationCommand;
    private final List<ExampleCommandCall> examples;
    private final List<CommandCall> beforeExamples;
    private final String specificationDescription;

    public XMLSpecification(CommandCall rootCommandNode, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
        this.rootCommandNode = rootCommandNode;
        if (!(rootCommandNode.getCommand() instanceof SpecificationCommand)) {
            throw new IllegalStateException("Expected root command to be a SpecificationCommand");
        }
        specificationCommand = (SpecificationCommand) rootCommandNode.getCommand();
        specificationCommand.start(rootCommandNode);
        specificationDescription = specificationCommand.getSpecificationDescription(rootCommandNode);

        this.examples = new ArrayList(examples);
        this.beforeExamples = new ArrayList(beforeExamples);
    }

    public void processNode(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {

        if (!node.getChildren().isEmpty()) {
            for (CommandCall before: beforeExamples) {
                SummarizingResultRecorder beforeResultRecorder = new SummarizingResultRecorder();
                beforeResultRecorder.setSpecificationDescription("Running before for example " + node.getExpression());
                before.getCommand().execute(before, evaluator, beforeResultRecorder, fixture);
                String errorText = null;
                if (beforeResultRecorder.hasExceptions()) {
                    errorText = SimpleFormatter.format("Exceptions occurred in the 'before' example in '%s'. See the output specification for details.\n",
                            testDescription
                    );
                } else if (beforeResultRecorder.getTotalCount() > 0) {
                    errorText = SimpleFormatter.format("Assertions were made in the 'before' example in '%s'.\n"
                            + "Assertions are not supported in the 'before' example.\n",
                            testDescription
                    );
                }
                if (errorText != null) {
                    System.err.println(errorText);
                    throw new ConcordionAssertionError(errorText, beforeResultRecorder);
                }
            }
        }

        node.execute(evaluator, resultRecorder, fixture);
    }

    public void process(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        processNode(rootCommandNode, evaluator, resultRecorder, fixture);
    }

    public void setFixture(FixtureType fixtureType) {
        if (hasExampleCommandNodes()) {
            testDescription = OUTER_EXAMPLE_NAME;
        } else {
            testDescription = fixtureType.getSpecificationDescription();
        }
    }

    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder, Fixture fixture) {
        if (testDescription.equals(example)) {
            processNode(rootCommandNode, evaluator, resultRecorder, fixture);
            return;
        }

        for (ExampleCommandCall commandCall: examples) {
            if (commandCall.getExampleName().equals(example)) {
                resultRecorder.setForExample(true);
                processNode(commandCall.getCommandCall(), evaluator, resultRecorder, fixture);
            }
        }
    }

    @Override
    public boolean hasExampleCommandNodes() {
        return examples.size() > 0;
    }

    @Override
    public String getSpecificationDescription() {
        return specificationDescription;
    }

    public List<String> getExampleNames() {

        List<String> commands = new ArrayList<String>();

        if (!rootCommandNode.getChildren().isEmpty()) {
            // Add the main spec first to increase the chance that it will be run first by jUnit.
            commands.add(testDescription);
        }

        for (ExampleCommandCall exampleCall: examples) {
            commands.add(exampleCall.getExampleName());
        }

        // If there are no examples and no commands, let's add the outer test so you have 1 test in the fixture
        if (commands.isEmpty()) {
            commands.add(testDescription);
        }

        return commands;
    }

    public void finish() {
        specificationCommand.finish(rootCommandNode);
    }
}
