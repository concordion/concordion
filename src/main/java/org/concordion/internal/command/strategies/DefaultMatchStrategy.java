package org.concordion.internal.command.strategies;

import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;

public class DefaultMatchStrategy extends RowsMatchStrategy {

    public static final String DEFAULT_STRATEGY_NAME = "Default";

    public DefaultMatchStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                                List<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    public void verify(Fixture fixture) {
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
            commandCall.getChildren().verify(evaluator, resultRecorder, fixture);
            index++;
        }

        for (; index < expectedRows.length; index++) {
            Row detailRow = expectedRows[index];
            resultRecorder.record(Result.FAILURE);
            announceMissingRow(detailRow.getElement());
        }
    }
}
