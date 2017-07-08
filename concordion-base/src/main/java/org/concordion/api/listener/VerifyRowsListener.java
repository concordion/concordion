package org.concordion.api.listener;

import java.util.EventListener;


public interface VerifyRowsListener extends EventListener {

    void expressionEvaluated(ExpressionEvaluatedEvent event);
    
    void missingRow(MissingRowEvent event);
    
    void surplusRow(SurplusRowEvent event);
}
