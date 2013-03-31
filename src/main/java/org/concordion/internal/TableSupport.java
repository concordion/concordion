package org.concordion.internal;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Element;

public class TableSupport {
    private final CommandCall tableCommandCall;
    private final Map<Integer, CommandCall> commandCallByColumn = new HashMap<Integer, CommandCall>();
    private Table table;

    public TableSupport(CommandCall tableCommandCall) {
        assert tableCommandCall.getElement().isNamed("table");
        this.tableCommandCall = tableCommandCall;
        this.table = new Table(tableCommandCall.getElement());
        populateCommandCallByColumnMap();
    }

    public int getColumnCount() {
        return getLastHeaderRow().getCells().length;
    }

    public Row[] getDetailRows() {
        return table.getDetailRows();
    }

    public void copyCommandCallsTo(Row detailRow) {
        int columnIndex = 0;
        for (Element cell : detailRow.getCells()) {
            CommandCall cellCall = commandCallByColumn.get(new Integer(columnIndex));
            if (cellCall != null) {
                cellCall.setElement(cell);
            }
            columnIndex++;
        }
    }

    private void populateCommandCallByColumnMap() {
        Row headerRow = getLastHeaderRow();
        CommandCallList children = tableCommandCall.getChildren();
        for (int i = 0; i < children.size(); i++) {
            CommandCall childCall = children.get(i);
            int columnIndex = headerRow.getIndexOfCell(childCall.getElement());
            if (columnIndex == -1) {
                throw new RuntimeException("Commands must be placed on <th> elements when using 'execute' or 'verifyRows' commands on a <table>.");
            }
            commandCallByColumn.put(new Integer(columnIndex), childCall);
        }
    }

    public Row getLastHeaderRow() {
        return table.getLastHeaderRow();
    }

    public Row addDetailRow() {
        return table.addDetailRow();
    }
}
