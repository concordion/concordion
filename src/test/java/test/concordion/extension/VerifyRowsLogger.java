package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.ConcordionBuilder;

public class VerifyRowsLogger implements VerifyRowsListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }
    
    public void expressionEvaluated(ExpressionEvaluatedEvent e) {
        stream.println("Evaluated '" + e.getElement().getAttributeValue("verifyRows", ConcordionBuilder.NAMESPACE_CONCORDION_2007) + "'");
    }
    
    public void missingRow(MissingRowEvent e) {
        stream.println("Missing Row '" + e.getRowElement().getText() + "'");
    }

    public void surplusRow(SurplusRowEvent e) {
        stream.println("Surplus Row '" + e.getRowElement().getText() + "'");
    }
}