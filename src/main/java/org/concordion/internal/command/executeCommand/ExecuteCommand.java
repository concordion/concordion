package org.concordion.internal.command.executeCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.internal.Row;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.Table;
import org.concordion.internal.TableSupport;

public class ExecuteCommand extends AbstractCommand {
    private List<ExecuteListener> executeListeners = new ArrayList<ExecuteListener>();
    private List<ExampleListener> exampleListeners = new ArrayList<ExampleListener>();

    public ExecuteCommand() {
    }

    @Override
    public void modifyCommandCallTree(CommandCall commandCall, List<CommandCall> examples, List<CommandCall> beforeExamples) {
        if (isTableElement(commandCall)) {
            performTableModification(commandCall);
        }

        if (isListElement(commandCall)) {
            performListModification(commandCall);
        }
    }

    private void performListModification(CommandCall commandCall) {
    }


    private Map<Integer, CommandCall> populateCommandCallByColumnMap(Table table, CommandCall tableCommandCall) {
        Map<Integer, CommandCall> commandCallByColumn = new HashMap<Integer, CommandCall>();

        Row headerRow = table.getLastHeaderRow();
        for (CommandCall childCall : tableCommandCall.getChildren().asCollection()) {
            int columnIndex = headerRow.getIndexOfCell(childCall.getElement());
            if (columnIndex == -1) {
                throw new RuntimeException("Commands must be placed on <th> elements when using 'execute' or 'verifyRows' commands on a <table>.");
            }
            commandCallByColumn.put(columnIndex, childCall);
        }

        return commandCallByColumn;
    }

    private void performTableModification(CommandCall commandCall) {


        /*

        We have to:
        * Copy the execute commandCall to each TR except for the header rows
        * Copy the TH commandCall to each TD
        * remove the table execute commandCall from the table
        * remove the TH commandCalls from the TH rows.
         */

        Table table = new Table(commandCall.getElement());
        Map<Integer, CommandCall> headerCommands = populateCommandCallByColumnMap(table, commandCall);

        // copy the execute to each detail row.
        Row headerRow = table.getLastHeaderRow();

        for (Row row : table.getDetailRows()) {
            CommandCall rowCommand = duplicateCommandForDifferentElement(commandCall, row.getElement());
            rowCommand.setParent(commandCall);

            for (int cellCount = 0; cellCount < row.getCells().length; cellCount++) {
                CommandCall headerCall = headerCommands.get(cellCount);

                if (headerCall != null) {
                    Element cellElement = row.getCells()[cellCount];
                    CommandCall cellCommand = duplicateCommandForDifferentElement(headerCall, cellElement);
                    cellCommand.setParent(rowCommand);
                }
            }
        }

        for (CommandCall headerCommand : headerCommands.values()) {
            headerCommand.setParent(null);
        }
    }

    private CommandCall duplicateCommandForDifferentElement(CommandCall commandCall, Element element) {

        String expression = commandCall.getExpression();
        if (expression.equals("")) {
            expression = element.getText();
        }

        return new CommandCall(null, commandCall.getCommand(), element, expression, commandCall.getResource());
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

    public void addExampleListener(ExampleListener listener) {
        exampleListeners.add(listener);
    }

    public void removeExampleListener(ExampleListener listener) {
        exampleListeners.remove(listener);
    }


//    @Override
//    public List<CommandCall> getExamples(CommandCall command) {
//        List<CommandCall> superExamples =  super.getExamples(command);
//
//        ExecuteCommandStrategy executeCommandStrategy = getStrategy(command);
//        List<CommandCall> strategyExamples = executeCommandStrategy.getExamples(command);
//
//        List<CommandCall> allExamples = new ArrayList<CommandCall>();
//        allExamples.addAll(superExamples);
//        allExamples.addAll(strategyExamples);
//
//        return allExamples;
//    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        ExecuteCommandStrategy executeCommandStrategy = getStrategy(commandCall);
        if (executeCommandStrategy != null) {
            executeCommandStrategy.execute(commandCall, evaluator, resultRecorder);
        } else {
            commandCall.getChildren().execute(evaluator, resultRecorder);
        }
    }

    private ExecuteCommandStrategy getStrategy(CommandCall commandCall) {
        ExecuteCommandStrategy executeCommandStrategy;
        if (isTableElement(commandCall)) {
            return null;
        } else if (isListElement(commandCall)) {
            executeCommandStrategy = new ExecuteCommandListStrategy();
        } else {
            executeCommandStrategy = new ExecuteCommandDefaultStrategy(this);
        }
        return executeCommandStrategy;
    }


    void announceExecuteCompleted(Element element) {
        for (ExecuteListener listener : executeListeners) {
            listener.executeCompleted(new ExecuteEvent(element));
        }
    }

    protected void announceBeforeExample(String exampleName, Element element, ResultRecorder resultRecorder) {
        for (ExampleListener listener : exampleListeners) {
            listener.beforeExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder) resultRecorder));
        }
    }

    protected void announceAfterExample(String exampleName, Element element, ResultRecorder resultRecorder) {
        for (ExampleListener listener : exampleListeners) {
            listener.afterExample(new ExampleEvent(exampleName, element, (SummarizingResultRecorder) resultRecorder));
        }
    }
}
