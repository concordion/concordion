package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.SpecificationByExample;

import java.util.ArrayList;
import java.util.List;

public class XMLSpecification implements SpecificationByExample {

    private String testDescription;

    private final CommandCall rootCommandNode;
    private final List<CommandCall> examples;
    private final List<CommandCall> beforeExamples;
    private Class<?> fixtureClass;

    public XMLSpecification(CommandCall rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
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
        fixtureClass = fixture;
        testDescription = Concordion.getDefaultFixtureClassName(fixture);
    }

    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder) {
        if (testDescription.equals(example)) {
            processNode(rootCommandNode, evaluator, resultRecorder);
            return;
        }

        for (CommandCall commandCall: examples) {
            if (makeJunitTestName(commandCall, fixtureClass).equals(example)) {
                resultRecorder.setForExample(true);
                processNode(commandCall, evaluator, resultRecorder);
            }
        }
    }

    public List<String> getExampleNames() {

        List<String> commands = new ArrayList<String>();

        for (CommandCall exampleCall: examples) {
            commands.add(makeJunitTestName(exampleCall, fixtureClass));
        }

        // we have to run the spec as a whole to ensure the HTML file is written.
        commands.add(testDescription);

        return commands;
    }

    private String makeJunitTestName(CommandCall exampleCall, Class<?> fixtureClass) {
        return Concordion.getDefaultFixtureClassName(fixtureClass, exampleCall.getExpression());
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
