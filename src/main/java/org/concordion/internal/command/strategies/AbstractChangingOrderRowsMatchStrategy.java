package org.concordion.internal.command.strategies;

import java.util.List;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.Fixture;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;

public abstract class AbstractChangingOrderRowsMatchStrategy extends RowsMatchStrategy {

    public AbstractChangingOrderRowsMatchStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                                                  List<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    public void verify(Fixture fixture) {
        announceExpressionEvaluated(commandCall.getElement());
        for (Row expectedRow : expectedRows) {
            Object row = findMatchingRow(expectedRow, fixture);
            tableSupport.copyCommandCallsTo(expectedRow);
            if (row != null) {
                evaluator.setVariable(loopVariableName, row);
                commandCall.getChildren().verify(evaluator, resultRecorder, fixture);
                actualRows.remove(row);
            } else {
                announceMissingRow(expectedRow.getElement());
            }
        }
        reportSurplusRows(fixture);
    }

    protected abstract Object findMatchingRow(Row expectedRow, Fixture fixture);

    private void reportSurplusRows(Fixture fixture) {
        for (Object surplusRow : actualRows) {
            evaluator.setVariable(loopVariableName, surplusRow);
            Row detailRow = tableSupport.addDetailRow();
            announceSurplusRow(detailRow.getElement());
            tableSupport.copyCommandCallsTo(detailRow);
            commandCall.getChildren().verify(evaluator, resultRecorder, fixture);
        }
    }
}
