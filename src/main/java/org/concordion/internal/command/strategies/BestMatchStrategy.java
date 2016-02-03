package org.concordion.internal.command.strategies;

import java.util.List;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.SummarizingResultRecorder;

public class BestMatchStrategy extends AbstractChangingOrderRowsMatchStrategy {

    public BestMatchStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                             List<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    protected Object findMatchingRow(Row expectedRow) {
        long bestResult = Integer.MIN_VALUE;
        Object bestMatchingRow = null;

        SummarizingResultRecorder backgroundResultRecorder = new SummarizingResultRecorder();
        for (Object row : actualRows) {

            tableSupport.copyCommandCallsTo(expectedRow.deepClone());
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
}
