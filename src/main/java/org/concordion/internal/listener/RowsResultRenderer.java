package org.concordion.internal.listener;

import org.concordion.api.Element;
import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.api.listener.RowsListener;

public class RowsResultRenderer implements RowsListener {

    public void expressionEvaluated(ExpressionEvaluatedEvent event) {
    }
    
    public void missingRow(MissingRowEvent event) {
        Element element = event.getRowElement();
        element.addStyleClass("missing");
    }

    public void surplusRow(SurplusRowEvent event) {
        Element element = event.getRowElement();
        element.addStyleClass("surplus");
    }
}
