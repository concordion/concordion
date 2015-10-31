package org.concordion.internal.command;

import java.util.Arrays;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.internal.ImplementationStatusChecker;
import org.concordion.internal.FailFastException;
import org.concordion.internal.SpecificationDescriber;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.util.Announcer;

/**
 * Created by tim on 2/07/15.
 */
public class ExampleCommand extends AbstractCommand {

	private Announcer<ExampleListener> listeners = Announcer.to(ExampleListener.class);
    private SpecificationDescriber specificationDescriber;

    public List<CommandCall> getExamples(CommandCall command) {
        return Arrays.asList(command);
    }

    public void addExampleListener(ExampleListener exampleListener) {
        listeners.addListener(exampleListener);
    }

    public void removeExampleListener(ExampleListener exampleListener) {
        listeners.removeListener(exampleListener);
    }
    
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void executeAsExample(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        String exampleName = node.getExpression();
        
        resultRecorder.setSpecificationDescription(
                specificationDescriber.getDescription(node.getResource(), exampleName));

        listeners.announce().beforeExample(new ExampleEvent(exampleName, node.getElement(), (SummarizingResultRecorder)resultRecorder));
        
        try {
            node.getChildren().processSequentially(evaluator, resultRecorder);
            Element aName = new Element("a");
            aName.addAttribute("name", exampleName);
            aName.addAttribute("id", exampleName); // html5 version
            node.getElement().prependChild(aName);

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
            
            listeners.announce().afterExample(new ExampleEvent(exampleName, node.getElement(), (SummarizingResultRecorder)resultRecorder));
        } catch (FailFastException e) {
            // Ignore - it'll be re-thrown later if necessary.
        } 
    }

    public boolean isExample() {
        return true;
    }

    public void setSpecificationDescriber(SpecificationDescriber specificationDescriber) {
        this.specificationDescriber = specificationDescriber;
    }
}
