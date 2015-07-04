package org.concordion.internal.command;

import org.concordion.api.*;
import org.concordion.internal.FailFastException;
import org.concordion.internal.SpecificationDescriber;
import org.concordion.internal.listener.SpecificationExporter;

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
            aName.addAttribute("id", "#"+node.getExpression()); // html5 version
            node.getElement().prependChild(aName);
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
