package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;

public class Table {
    private final Element tableElement;

    public Table(Element tableElement) {
        assert tableElement.isNamed("table");
        this.tableElement = tableElement;
    }

    public Row getLastHeaderRow() {
        Row[] headerRows = getHeaderRows();
        if (headerRows.length == 0) {
            throw new RuntimeException("Table has no header row (i.e. no row containing only <th> elements)");
        } else {
            return headerRows[headerRows.length - 1];
        }
    }

    private Row[] getHeaderRows() {
        List<Row> headerRows = new ArrayList<Row>();
        for (Row row : getRows()) {
            if (row.isHeaderRow()) {
                headerRows.add(row);
            }
        }
        return headerRows.toArray(new Row[headerRows.size()]);
    }

    public Row[] getDetailRows() {
        List<Row> detailRows = new ArrayList<Row>();
        for (Row row : getRows()) {
            if (!row.isHeaderRow()) {
                detailRows.add(row);
            }
        }
        return detailRows.toArray(new Row[detailRows.size()]);
    }
    
    private Row[] getRows() {
        List<Row> rows = new ArrayList<Row>();
        for (Element rowElement : tableElement.getDescendantElements("tr")) {
            rows.add(new Row(rowElement));
        }
        return rows.toArray(new Row[rows.size()]);
    }
    
    public Row addDetailRow() {
        Element rowElement = new Element("tr");
        
        Element tbody = tableElement.getFirstChildElement("tbody");
        if (tbody != null) {
            tbody.appendChild(rowElement);
        } else {
            tableElement.appendChild(rowElement);
        }
        
        for (int i = 0; i < getColumnCount(); i++) {
            rowElement.appendChild(new Element("td"));
        }
        return new Row(rowElement);
    }

    private int getColumnCount() {
        return getLastHeaderRow().getCells().length;
    }
}
