package org.concordion.internal.command.strategies;

import org.concordion.api.*;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.util.Announcer;

public class Default extends VerifyRowsStrategy {
    public Default(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                   Announcer<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    public void verify() {
        announceExpressionEvaluated(commandCall.getElement());

        int index = 0;
        for (Object loopVar : actualRows) {
            evaluator.setVariable(loopVariableName, loopVar);
            Row detailRow;
            if (expectedRows.length > index) {
                detailRow = expectedRows[index];
            } else {
                detailRow = tableSupport.addDetailRow();
                announceSurplusRow(detailRow.getElement());
            }
            tableSupport.copyCommandCallsTo(detailRow);
            commandCall.getChildren().verify(evaluator, resultRecorder);
            index++;
        }

        for (; index < expectedRows.length; index++) {
            Row detailRow = expectedRows[index];
            resultRecorder.record(Result.FAILURE);
            announceMissingRow(detailRow.getElement());
        }
    }
}
