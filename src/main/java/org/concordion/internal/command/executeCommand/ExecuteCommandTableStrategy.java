package org.concordion.internal.command.executeCommand;

import org.concordion.api.*;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;
import org.concordion.internal.command.ExampleCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tim on 12/06/16.
 */
public class ExecuteCommandTableStrategy implements ExecuteCommandStrategy {

    private final ExecuteCommand executeCommand;

    public ExecuteCommandTableStrategy(ExecuteCommand executeCommand) {
        this.executeCommand = executeCommand;
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        TableSupport tableSupport = new TableSupport(commandCall);

        // check if this table is in "example" mode.
        if (hasExamples(tableSupport)) {
            return;
        }

        Row[] detailRows = tableSupport.getDetailRows();
        for (Row detailRow : detailRows) {
            if (detailRow.getCells().length != tableSupport.getColumnCount()) {
                throw new RuntimeException("The <table> 'execute' command only supports rows with an equal number of columns.");
            }
            commandCall.setElement(detailRow.getElement());
            tableSupport.copyCommandCallsTo(detailRow);
            commandCall.execute(evaluator, resultRecorder);
        }
    }

    private boolean hasExamples(TableSupport tableSupport) {
        // if any of the columns have the exampleName tag, then we are running as examples.
        return getExampleColumn(tableSupport) >= 0;
    }

    private int getExampleColumn(TableSupport tableSupport) {
        Row headerRow = tableSupport.getLastHeaderRow();
        for (Element e : headerRow.getCells()) {
            String exampleText = e.getAttributeValue("example", ConcordionBuilder.NAMESPACE_CONCORDION_2007);
            if (exampleText != null) {
                // OK. This is an "examplised" table. Return this column index.
                int exampleNameIndex = headerRow.getIndexOfCell(e);
                return exampleNameIndex;
            }
        }
        return -1;
    }

    @Override
    public List<CommandCall> getExamples(CommandCall commandCall) {

        TableSupport tableSupport = new TableSupport(commandCall);

        // we could call the hasExamples method, but we need the index later so may as well cache it.
        int exampleNameIndex = getExampleColumn(tableSupport);

        if (exampleNameIndex < 0) {
            return Collections.emptyList();
        }

        List<CommandCall> tableExamples = new ArrayList<CommandCall>();
        for (Row detailRow : tableSupport.getDetailRows()) {

            // for each row, we're going to get the text of our example name index.
            Element nameElement = detailRow.getCells()[exampleNameIndex];
            String exampleName = nameElement.getText();
            Command newCommand = new TableRowExampleCommand(detailRow, tableSupport, commandCall, executeCommand, exampleName);
            CommandCall newCall = new CommandCall(newCommand, commandCall.getElement(), exampleName, commandCall.getResource());
            tableExamples.add(newCall);
        }
        return tableExamples;
    }
}
