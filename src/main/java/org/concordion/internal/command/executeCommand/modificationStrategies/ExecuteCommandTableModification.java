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
            We set the bypass flag on the main <table> node so it's not executed anymore. We
            can't really remove it because if we remove it, we'll have to add all the new children to
            this node's parent node. However, the 'modify' method is currently processing the parent node
            and has made a copy of the list of children (to avoid a concurrent modification exception).

            This means that any children added to the parent node won't have 'modify' called on them. And if
            any of the children are examples, then they won't be processed properly.
         */
        commandCall.setBypassExecution(true);

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

    @Override
    public boolean originalNodeShouldBypassExecution() {
        return true;
    }


}
