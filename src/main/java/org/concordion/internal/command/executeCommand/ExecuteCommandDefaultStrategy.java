package org.concordion.internal.command.executeCommand;

import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.command.ExampleCommand;

import java.util.Collections;
import java.util.List;

/**
 * Created by tim on 12/06/16.
 */
class ExecuteCommandDefaultStrategy implements ExecuteCommandStrategy {

    private ExecuteCommand executeCommand;

    public ExecuteCommandDefaultStrategy(ExecuteCommand executeCommand) {
        this.executeCommand = executeCommand;
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        evaluator.evaluate(commandCall.getExpression());
        childCommands.execute(evaluator, resultRecorder);
        executeCommand.announceExecuteCompleted(commandCall.getElement());
        childCommands.verify(evaluator, resultRecorder);
    }

    @Override
    public List<CommandCall> getExamples(CommandCall command) {
        return Collections.emptyList();
    }
}
