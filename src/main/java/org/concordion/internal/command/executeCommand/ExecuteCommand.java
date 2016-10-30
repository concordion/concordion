package org.concordion.internal.command.executeCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.concordion.api.*;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.api.ExampleCommandCall;
import org.concordion.internal.Row;
import org.concordion.internal.Table;

public class ExecuteCommand extends AbstractCommand {
    private List<ExecuteListener> executeListeners = new ArrayList<ExecuteListener>();
    private List<ExampleListener> exampleListeners = new ArrayList<ExampleListener>();

    public ExecuteCommand() {
    }

    @Override
    public void modifyCommandCallTree(CommandCall commandCall, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
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
        Row[] detailRows = table.getDetailRows();
        for (int i = 0; i < detailRows.length; i++) {
            Row row = detailRows[i];
            Element[] cells = row.getCells();

            if (cells.length != table.getLastHeaderRow().getCells().length) {
                throw new RuntimeException("The <table> 'execute' command only supports rows with an equal number of columns. Detail row " + (i + 1) + " has a different number of columns to the last header row");
            }

            CommandCall rowCommand = duplicateCommandForDifferentElement(commandCall, row.getElement());
            rowCommand.transferToParent(commandCall);

            for (int cellCount = 0; cellCount < cells.length; cellCount++) {
                CommandCall headerCall = headerCommands.get(cellCount);

                if (headerCall != null) {
                    Element cellElement = cells[cellCount];
                    CommandCall cellCommand = duplicateCommandForDifferentElement(headerCall, cellElement);
                    cellCommand.transferToParent(rowCommand);
                }
            }
        }

        for (CommandCall headerCommand : headerCommands.values()) {
            headerCommand.transferToParent(null);
        }
    }

    private CommandCall duplicateCommandForDifferentElement(CommandCall commandCall, Element element) {
        return new CommandCall(
                null,
                commandCall.getCommand(),
                element,
                commandCall.getExpression(),
                commandCall.getResource());
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
}
