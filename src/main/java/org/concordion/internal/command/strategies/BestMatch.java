package org.concordion.internal.command.strategies;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.VerifyRowsStrategy;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.util.Announcer;

public class BestMatch extends VerifyRowsStrategy {

    public BestMatch(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                     Announcer<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    public void verify() {
        announceExpressionEvaluated(commandCall.getElement());
        for (Row expectedRow : expectedRows) {
            Object row = findBestMatchingRow(expectedRow);
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

    private Object findBestMatchingRow(Row expectedRow) {
        long bestResult = Integer.MIN_VALUE;
        Object bestMatchingRow = null;

        SummarizingResultRecorder backgroundResultRecorder = new SummarizingResultRecorder();
        for (Object row : actualRows) {

            Row clone = expectedRow.deepClone();
            tableSupport.copyCommandCallsTo(clone);

            evaluator.setVariable(loopVariableName, row);
            commandCall.getChildren().verify(evaluator, backgroundResultRecorder);

            long total = backgroundResultRecorder.getTotalCount();
            long success = backgroundResultRecorder.getSuccessCount();

            //fully matched
            if (total == success) {
                return row;
            }

            //this row has same number of successful/failed fields as other one so that we do not know for sure what is matching candidate
            if (success == bestResult) {
                bestMatchingRow = null;
            }

            //next most promising row
            if (success > 0 && success > bestResult) {
                bestMatchingRow = row;
                bestResult = success;
            }

            backgroundResultRecorder.reset();
        }
        return bestMatchingRow;
    }

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
