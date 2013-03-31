package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;

public class Row {
    private final Element rowElement;

    Row(Element rowElement) {
        assert rowElement.isNamed("tr");
        this.rowElement = rowElement;
    }
    
    public boolean isHeaderRow() {
        for (Element cell : getCells()) {
            if (cell.isNamed("td")) {
                return false;
            }
        }
        return getCells().length > 0;
    }

    public Element getElement() {
        return rowElement;
    }
    
    public Element[] getCells() {
        List<Element> cells = new ArrayList<Element>();
        for (Element childElement : rowElement.getChildElements()){
            if (childElement.isNamed("td") || childElement.isNamed("th")) {
                cells.add(childElement);
            }
        }
        return cells.toArray(new Element[0]);
    }

    public int getIndexOfCell(Element cell) {
        Element[] cells = getCells();
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].equals(cell)) {
                return i;
            }
        }
        return -1;
    }
}
