package org.concordion.internal.command.executeCommand;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.api.ExampleCommandCall;
import org.concordion.internal.command.executeCommand.modificationStrategies.ExecuteCommandModification;
import org.concordion.internal.command.executeCommand.modificationStrategies.ExecuteCommandListModification;
import org.concordion.internal.command.executeCommand.modificationStrategies.ExecuteCommandTableModification;

public class ExecuteCommand extends AbstractCommand {
    private List<ExecuteListener> executeListeners = new ArrayList<ExecuteListener>();

    public ExecuteCommand() {
    }

    @Override
    public void modifyCommandCallTree(CommandCall commandCall, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {

        ExecuteCommandModification modification = determineModificationStrategy(commandCall);

        if (modification != null) {
            modification.performModification(commandCall, examples, beforeExamples);
        }
    }

    private ExecuteCommandModification determineModificationStrategy(CommandCall commandCall) {
        if (isTableElement(commandCall)) {
            return new ExecuteCommandTableModification();
        }

        if (isListElement(commandCall)) {
            return new ExecuteCommandListModification();
        }

        return null;
    }


    private boolean isTableElement(CommandCall commandCall) {
        return commandCall.getElement().isNamed("table");
    }

    private boolean isListElement(CommandCall commandCall) {
        return commandCall.getElement().isNamed("ol") || commandCall.getElement().isNamed("ul");
    }

    public void addExecuteListener(ExecuteListener listener) {
        executeListeners.add(listener);
    }

    public void removeExecuteListener(ExecuteListener listener) {
        executeListeners.remove(listener);
    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        // bypass normal execution if the modification required it.
        if (commandCall.bypassExecution()) {
            commandCall.getChildren().execute(evaluator, resultRecorder);
            return;
        }

        normalExecution(commandCall, evaluator, resultRecorder);
    }

    private void normalExecution(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        evaluator.evaluate(commandCall.getExpression());
        childCommands.execute(evaluator, resultRecorder);
        announceExecuteCompleted(commandCall.getElement());
        childCommands.verify(evaluator, resultRecorder);
    }


    private void announceExecuteCompleted(Element element) {
        for (ExecuteListener listener : executeListeners) {
            listener.executeCompleted(new ExecuteEvent(element));
        }
    }
}
