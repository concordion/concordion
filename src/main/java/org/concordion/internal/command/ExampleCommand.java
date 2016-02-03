package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.internal.FailFastException;
import org.concordion.internal.ImplementationStatusChecker;
import org.concordion.internal.SpecificationDescriber;
import org.concordion.internal.SummarizingResultRecorder;

public class ExampleCommand extends AbstractCommand {

	private List<ExampleListener> listeners = Collections.synchronizedList(new ArrayList<ExampleListener>());
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
    
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void executeAsExample(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        String exampleName = node.getExpression();
        
        resultRecorder.setSpecificationDescription(
                specificationDescriber.getDescription(node.getResource(), exampleName));

        announceBeforeExample(exampleName, node.getElement(), resultRecorder);
        
        try {
            node.getChildren().processSequentially(evaluator, resultRecorder);
        } catch (FailFastException f) {
            // Ignore - it'll be re-thrown later by the implementation status checker if necessary.
        }
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

        announceAfterExample(exampleName, node.getElement(), resultRecorder);
    }

    public boolean isExample() {
        return true;
    }

    public void setSpecificationDescriber(SpecificationDescriber specificationDescriber) {
        this.specificationDescriber = specificationDescriber;
    }
    
    private void announceBeforeExample(String exampleName, Element element,	ResultRecorder resultRecorder) {
		for (ExampleListener listener : listeners) {
			listener.beforeExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder)resultRecorder));
		}
	}
    
    private void announceAfterExample(String exampleName, Element element, ResultRecorder resultRecorder) {
		for (ExampleListener listener : listeners) {
			listener.afterExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder)resultRecorder));
		}
	}
}
