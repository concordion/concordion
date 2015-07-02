package org.concordion.internal.command;

import org.concordion.api.*;
import org.concordion.internal.FailFastException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 2/07/15.
 */
public class ExampleCommand extends AbstractCommand {

    public List<CommandCall> getExamples(CommandCall command) {
        return Arrays.asList(command);
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void executeAsExample(CommandCall node, Evaluator evaluator, ResultRecorder resultRecorder) {
        try {
            node.getChildren().processSequentially(evaluator, resultRecorder);
        } catch (FailFastException e) {
            // Ignore - it'll be re-thrown later if necessary.
        }
    }

    public boolean isExample() {
        return true;
    }

}
