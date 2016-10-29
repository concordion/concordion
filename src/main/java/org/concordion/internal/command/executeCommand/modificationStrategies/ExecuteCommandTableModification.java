package org.concordion.internal.command.executeCommand.modificationStrategies;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.ExampleCommandCall;
import org.concordion.internal.Row;
import org.concordion.internal.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tim on 30/10/16.
 */
public class ExecuteCommandTableModification extends ExecuteCommandModification {

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

    public void performModification(CommandCall commandCall, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {

        /*

        We have to:
        * Copy the execute commandCall to each TR except for the header rows
        * Copy the TH commandCall to each TD
        * remove the table execute commandCall from the table
        * remove the TH commandCalls from the TH rows.
         */

        CommandCall newParent = commandCall.getParent();

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

            for (int cellCount = 0; cellCount < cells.length; cellCount++) {
                CommandCall headerCall = headerCommands.get(cellCount);

                if (headerCall != null) {
                    Element cellElement = cells[cellCount];
                    CommandCall cellCommand = duplicateCommandForDifferentElement(headerCall, cellElement);
                    cellCommand.transferToParent(rowCommand);
                }
            }

            rowCommand.transferToParent(newParent);
            rowCommand.modifyTree(examples, beforeExamples);
        }

        for (CommandCall call : headerCommands.values()) {
            call.transferToParent(null);
        }

        commandCall.transferToParent(null);
    }

    @Override
    public boolean originalNodeShouldBypassExecution() {
        return true;
    }


}
