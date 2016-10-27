package org.concordion.internal.command.executeCommand;

import org.concordion.api.*;
import org.concordion.internal.ImplementationStatusChecker;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;
import org.concordion.internal.command.ExampleCommand;

import java.util.Collections;
import java.util.List;

/**
 * Created by tim on 12/06/16.
 */
public class TableRowExampleCommand implements Command {
    private final Row detailRow;
    private final TableSupport tableSupport;
    private final CommandCall tableExecuteCommand;
    private final ExecuteCommand executeCommand;
    private final String exampleName;

    public TableRowExampleCommand(Row detailRow, TableSupport tableSupport, CommandCall tableExecuteCommand, ExecuteCommand executeCommand, String exampleName) {
        this.detailRow = detailRow;
        this.tableSupport = tableSupport;
        this.tableExecuteCommand = tableExecuteCommand;
        this.executeCommand = executeCommand;
        this.exampleName = exampleName;
    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        executeCommand.setUp(commandCall, evaluator, resultRecorder);
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        executeCommand.verify(commandCall, evaluator, resultRecorder);
    }

    @Override
    public void modifyCommandCallTree(CommandCall element, List<CommandCall> examples, List<CommandCall> beforeExamples) {

    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        executeCommand.announceBeforeExample(exampleName, detailRow.getElement(), resultRecorder);

        if (detailRow.getCells().length != tableSupport.getColumnCount()) {
            throw new RuntimeException("The <table> 'execute' command only supports rows with an equal number of columns.");
        }

        tableExecuteCommand.setElement(detailRow.getElement());
        tableSupport.copyCommandCallsTo(detailRow);

        ExampleCommand.setupCommandForExample(tableExecuteCommand, resultRecorder, exampleName);

        tableExecuteCommand.execute(evaluator, resultRecorder);

        executeCommand.announceAfterExample(exampleName, detailRow.getElement(), resultRecorder);
    }
}
