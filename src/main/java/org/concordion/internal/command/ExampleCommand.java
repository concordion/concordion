package org.concordion.internal.command;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureState;
import org.concordion.internal.SpecificationDescriber;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.util.Announcer;

import java.util.Arrays;
import java.util.List;

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

        resultRecorder.setSpecificationDescription(
                specificationDescriber.getDescription(node.getResource(), node.getExpression()));

        listeners.announce().beforeExample(new ExampleEvent(node, resultRecorder));
        
        try {
            node.getChildren().processSequentially(evaluator, resultRecorder);
            Element aName = new Element("a");
            aName.addAttribute("name", "#"+node.getExpression());
            aName.addAttribute("id", "#" + node.getExpression()); // html5 version
            node.getElement().prependChild(aName);

            String params = node.getParameter("state");
            if (params != null) {
                ResultModifier resultModifier = ResultModifier.getModifier(params);
                resultRecorder.setResultModifier(resultModifier);
                // let's be really nice and add the fixture state text into the element itself.
                FixtureState fixtureState = FixtureState.getFixtureState(null, resultModifier);

                String note;
                if (fixtureState != null) {
                    note = fixtureState.printNoteToString();
                } else {
                    note = "Invalid state expression " + params;
                } 
                Element fixtureNode = new Element("p");
                fixtureNode.appendText(note);
                node.getElement().prependChild(fixtureNode);
            }
            
            listeners.announce().afterExample(new ExampleEvent(node, resultRecorder));
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
