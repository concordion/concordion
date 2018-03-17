package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.internal.*;

public class ExampleCommand extends AbstractCommand {

	private List<ExampleListener> listeners = new ArrayList<ExampleListener>();
    private SpecificationDescriber specificationDescriber;

    public List<CommandCall> getExamples(CommandCall command) {
        return Arrays.asList(command);
    }

    public void addExampleListener(ExampleListener exampleListener) {
        listeners.add(exampleListener);
    }

    public void removeExampleListener(ExampleListener exampleListener) {
        listeners.remove(exampleListener);
    }

    public void execute(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {

        String exampleName = getExampleName(node);
        boolean isBeforeExample = isBeforeExample(node);

        resultRecorder.setSpecificationDescription(
                specificationDescriber.getDescription(node.getResource(), exampleName));

        if (!isBeforeExample) {
            Fixture fixture = XMLSpecification.FIXTURE_HOLDER.get();
            announceBeforeExample(exampleName, node.getElement(), resultRecorder, fixture);
        }

        try {
            node.getChildren().processSequentially(evaluator, resultRecorder);
        } catch (FailFastException f) {
            // Ignore - it'll be re-thrown later by the implementation status checker if necessary.
        }
        setupCommandForExample(node, resultRecorder, exampleName);

        if (!isBeforeExample) {
            Fixture fixture = XMLSpecification.FIXTURE_HOLDER.get();
            announceAfterExample(exampleName, node.getElement(), resultRecorder, fixture);
        }
    }

    private String getExampleName(CommandCall node) {
        String exampleName = node.getExpression();

        // use the contents of the example if there is no name.
        if ("".equals(exampleName) && node.getElement().isNamed("td")) {
            exampleName = node.getElement().getText();
        }
        return exampleName;
    }

    @Override
    public void modifyCommandCallTree(CommandCall element, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
        super.modifyCommandCallTree(element, examples, beforeExamples);

        CommandCall oldParent = element.getParent();
        element.transferToParent(null);

        // we have to pull the example command to be the parent of the execute command
        // on the TR element
        if (element.getElement().isNamed("td")) {
            oldParent.transferToParent(element);
        }

        if (this.isBeforeExample(element)) {
            beforeExamples.add(element);
        } else {
            examples.add(new ExampleCommandCall(this.getExampleName(element), element));
        }
    }

    protected boolean isBeforeExample(CommandCall element) {
        return element.getExpression().equals("before");
    }

    public static void setupCommandForExample(CommandCall node, ResultRecorder resultRecorder, String exampleName) {
        node.getElement().addAttribute("id", exampleName);

        String params = node.getParameter("status");
        if (params != null) {
            ImplementationStatus implementationStatus = ImplementationStatus.implementationStatusFor(params);
            resultRecorder.setImplementationStatus(implementationStatus);
            // let's be really nice and add the implementation status text into the element itself.
            ImplementationStatusChecker checker = ImplementationStatusChecker.implementationStatusCheckerFor(implementationStatus);

            String note;
            if (checker != null) {
                note = checker.printNoteToString();
            } else {
                note = "Invalid status expression " + params;
            }
            Element fixtureNode = new Element("p");
            fixtureNode.appendText(note);
            node.getElement().prependChild(fixtureNode);
        }
    }

    public void setSpecificationDescriber(SpecificationDescriber specificationDescriber) {
        this.specificationDescriber = specificationDescriber;
    }

    private void announceBeforeExample(String exampleName, Element element, ResultRecorder resultRecorder, Fixture fixture) {
		for (ExampleListener listener : listeners) {
			listener.beforeExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder)resultRecorder, fixture));
		}
	}

    private void announceAfterExample(String exampleName, Element element, ResultRecorder resultRecorder, Fixture fixture) {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).afterExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder)resultRecorder, fixture));
        }
	}
}
