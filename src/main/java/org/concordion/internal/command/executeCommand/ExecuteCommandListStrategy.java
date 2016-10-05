package org.concordion.internal.command.executeCommand;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.ListEntry;
import org.concordion.internal.ListSupport;
import org.concordion.internal.command.ExampleCommand;

import java.util.Collections;
import java.util.List;

/**
 * Created by tim on 12/06/16.
 */
class ExecuteCommandListStrategy implements ExecuteCommandStrategy {

    private static final String LEVEL_VARIABLE = "#LEVEL";

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        increaseLevel(evaluator);
        ListSupport listSupport = new ListSupport(commandCall);
        for (ListEntry listEntry : listSupport.getListEntries()) {
            commandCall.setElement(listEntry.getElement());
            if (listEntry.isItem()) {
                commandCall.execute(evaluator, resultRecorder);
            }
            if (listEntry.isList()) {
                execute(commandCall, evaluator, resultRecorder);
            }
        }
        decreaseLevel(evaluator);
    }

    @Override
    public List<CommandCall> getExamples(CommandCall command) {
        return Collections.emptyList();
    }

    private void increaseLevel(Evaluator evaluator) {
        Integer value = (Integer) evaluator.getVariable(LEVEL_VARIABLE);
        if (value == null) {
            value = 0;
        }
        evaluator.setVariable(LEVEL_VARIABLE, value + 1);
    }

    private void decreaseLevel(Evaluator evaluator) {
        Integer value = (Integer) evaluator.getVariable(LEVEL_VARIABLE);
        evaluator.setVariable(LEVEL_VARIABLE, value - 1);
    }

}
