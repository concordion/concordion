package org.concordion.internal.command.strategies;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.RowsMatchStrategy;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.util.Announcer;

public abstract class AbstractChangingOrderRowsMatchStrategy extends RowsMatchStrategy {

    public AbstractChangingOrderRowsMatchStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                                                  Announcer<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    public void verify() {
        announceExpressionEvaluated(commandCall.getElement());
        for (Row expectedRow : expectedRows) {
            Object row = findMatchingRow(expectedRow);
            tableSupport.copyCommandCallsTo(expectedRow);
            if (row != null) {
                evaluator.setVariable(loopVariableName, row);
                commandCall.getChildren().verify(evaluator, resultRecorder);
                actualRows.remove(row);
            } else {
                announceMissingRow(expectedRow.getElement());
            }
        }
        reportSurplusRows();
    }

    protected abstract Object findMatchingRow(Row expectedRow);

    private void reportSurplusRows() {
        for (Object surplusRow : actualRows) {
            evaluator.setVariable(loopVariableName, surplusRow);
            Row detailRow = tableSupport.addDetailRow();
            announceSurplusRow(detailRow.getElement());
            tableSupport.copyCommandCallsTo(detailRow);
            commandCall.getChildren().verify(evaluator, resultRecorder);
        }
    }
}
