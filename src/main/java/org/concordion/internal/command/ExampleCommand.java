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
    private ImplementationStatusModifier implementationStatusModifier;

    public List<CommandCall> getExamples(CommandCall command) {
        return Arrays.asList(command);
    }

    public void addExampleListener(ExampleListener exampleListener) {
        listeners.add(exampleListener);
    }

    public void removeExampleListener(ExampleListener exampleListener) {
        listeners.remove(exampleListener);
    }

    public void execute(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {

        String exampleName = getExampleName(node);
        boolean isBeforeExample = isBeforeExample(node);

        resultRecorder.setSpecificationDescription(
                specificationDescriber.getDescription(node.getResource(), exampleName));

        ImplementationStatus status = getImplementationStatus(node);

        if (!isBeforeExample && status != ImplementationStatus.IGNORED) {
            announceBeforeExample(exampleName, node.getElement(), resultRecorder, fixture);
        }

        try {
            resultRecorder.setImplementationStatus(status);
            if (status == ImplementationStatus.IGNORED) {
                resultRecorder.record(Result.IGNORED);
            } else {
                node.getChildren().processSequentially(evaluator, resultRecorder, fixture);
            }
        } catch (FailFastException f) {
            // Ignore - it'll be re-thrown later by the implementation status checker if necessary.
        }
        setupCommandForExample(node, resultRecorder, exampleName);

        if (!isBeforeExample && status != ImplementationStatus.IGNORED) {
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

    private ImplementationStatus getImplementationStatus(CommandCall node) {
        // by default the implementation status is expected to pass
        ImplementationStatus implementationStatus = ImplementationStatus.EXPECTED_TO_PASS;
        // if there's a status param, it overrides expected to pass
        String params = node.getParameter("status");
        if (params != null) {
            implementationStatus = ImplementationStatus.implementationStatusFor(params);
        }
        // if there's a status modifier and there's a status for the example, it overrides status param
        if (implementationStatusModifier != null) {
            ImplementationStatus runtimeImplementation = implementationStatusModifier.getStatusForExample(exampleDefinition(node.getElement()));
            if (runtimeImplementation != null) {
                implementationStatus = runtimeImplementation;
            }
        }
        return implementationStatus;
    }

    public static void setupCommandForExample(CommandCall node, ResultRecorder resultRecorder, String exampleName) {
        node.getElement().addAttribute("id", exampleName);

        // let's be really nice and add the implementation status text into the element itself.
        ImplementationStatusChecker checker = ImplementationStatusChecker.implementationStatusCheckerFor(resultRecorder.getImplementationStatus());

        Element fixtureNode = new Element("p");
        fixtureNode.appendText(checker.printNoteToString());
        node.getElement().prependChild(fixtureNode);
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

    public void setImplementationStatusModifier(ImplementationStatusModifier implementationStatusModifier) {
        this.implementationStatusModifier = implementationStatusModifier;
    }

    private static ExampleDefinition exampleDefinition(final Element element) {
        return new ExampleDefinition() {
            @Override
            public String getName() {
                return element.getAttributeValue("example", ConcordionBuilder.NAMESPACE_CONCORDION_2007);
            }

            @Override
            public String getAttributeValue(String name) {
                return element.getAttributeValue(name);
            }

            @Override
            public String getAttributeValue(String localName, String namespaceURI) {
                return element.getAttributeValue(localName, namespaceURI);
            }
        };
    }

}
