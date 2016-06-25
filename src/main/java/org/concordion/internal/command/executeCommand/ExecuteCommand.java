package org.concordion.internal.command.executeCommand;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.command.ExampleCommand;

public class ExecuteCommand extends AbstractCommand {
    private List<ExecuteListener> listeners = new ArrayList<ExecuteListener>();
    private List<ExampleListener> exampleListeners = new ArrayList<ExampleListener>();

    public ExecuteCommand() {
    }

    public void addExecuteListener(ExecuteListener listener) {
        listeners.add(listener);
    }

    public void removeExecuteListener(ExecuteListener listener) {
        listeners.remove(listener);
    }

    public void addExampleListener(ExampleListener listener) {
        exampleListeners.add(listener);
    }

    public void removeExampleListener(ExampleListener listener) {
        exampleListeners.remove(listener);
    }


    @Override
    public List<CommandCall> getExamples(CommandCall command) {
        List<CommandCall> superExamples =  super.getExamples(command);

        ExecuteCommandStrategy executeCommandStrategy = getStrategy(command);
        List<CommandCall> strategyExamples = executeCommandStrategy.getExamples(command);

        List<CommandCall> allExamples = new ArrayList<CommandCall>();
        allExamples.addAll(superExamples);
        allExamples.addAll(strategyExamples);

        return allExamples;
    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        ExecuteCommandStrategy executeCommandStrategy = getStrategy(commandCall);
        executeCommandStrategy.execute(commandCall, evaluator, resultRecorder);
    }

    private ExecuteCommandStrategy getStrategy(CommandCall commandCall) {
        ExecuteCommandStrategy executeCommandStrategy;
        if (commandCall.getElement().isNamed("table")) {
            executeCommandStrategy = new ExecuteCommandTableStrategy(this);
        } else if (commandCall.getElement().isNamed("ol") || commandCall.getElement().isNamed("ul")) {
        	executeCommandStrategy = new ExecuteCommandListStrategy();
        } else {
            executeCommandStrategy = new ExecuteCommandDefaultStrategy(this);
        }
        return executeCommandStrategy;
    }

    void announceExecuteCompleted(Element element) {
    	for (ExecuteListener listener : listeners) {
    		listener.executeCompleted(new ExecuteEvent(element));
		}
    }

    protected void announceBeforeExample(String exampleName, Element element, ResultRecorder resultRecorder) {
        for (ExampleListener listener : exampleListeners) {
            listener.beforeExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder)resultRecorder));
        }
    }

    protected void announceAfterExample(String exampleName, Element element, ResultRecorder resultRecorder) {
        for (ExampleListener listener : exampleListeners) {
            listener.afterExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder)resultRecorder));
        }
    }
}
