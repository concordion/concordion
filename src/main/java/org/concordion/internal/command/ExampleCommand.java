package org.concordion.internal.command;

import org.concordion.api.*;
import org.concordion.internal.FailFastException;
import org.concordion.internal.Fixture;
import org.concordion.internal.ExpectedState;
import org.concordion.internal.SpecificationDescriber;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 2/07/15.
 */
public class ExampleCommand extends AbstractCommand {

    private SpecificationDescriber specificationDescriber;

    public List<CommandCall> getExamples(CommandCall command) {
        return Arrays.asList(command);
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void executeAsExample(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {

        resultRecorder.setSpecificationDescription(
                specificationDescriber.getDescription(node.getResource(), node.getExpression()));

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
                ExpectedState expectedState = ExpectedState.getExpectedStateFor(resultModifier);

                String note;
                if (expectedState != null) {
                    note = expectedState.printNoteToString();
                } else {
                    note = "Invalid state expression " + params;
                } 
                Element fixtureNode = new Element("p");
                fixtureNode.appendText(note);
                node.getElement().prependChild(fixtureNode);
            }

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
