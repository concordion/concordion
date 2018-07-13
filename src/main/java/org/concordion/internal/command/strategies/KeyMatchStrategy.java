package org.concordion.internal.command.strategies;

import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.SummarizingResultRecorder;

public class KeyMatchStrategy extends AbstractChangingOrderRowsMatchStrategy {

    public KeyMatchStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                            List<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        super(commandCall, evaluator, resultRecorder, listeners, loopVariableName, actualRows);
    }

    @Override
    protected Object findMatchingRow(Row expectedRow, Fixture fixture) {

        Element[] headerCells = tableSupport.getLastHeaderRow().getCells();
        CommandCallList childrenCalls = commandCall.getChildren();

        assert headerCells.length == childrenCalls.size();

        SummarizingResultRecorder backgroundResultRecorder = new SummarizingResultRecorder();
        for (Object row : actualRows) {

            tableSupport.copyCommandCallsTo(expectedRow.deepClone());
            evaluator.setVariable(loopVariableName, row);

            long total = 0;
            long success = 0;

            for (CommandCall columnCommand : childrenCalls.asCollection()) {
                columnCommand.verify(evaluator, backgroundResultRecorder, fixture);

                String matchingRole = columnCommand.getParameter("matchingRole", "matching-role");
                if (matchingRole != null && matchingRole.equalsIgnoreCase("key")) {
                    total += backgroundResultRecorder.getTotalCount();
                    success += backgroundResultRecorder.getSuccessCount();
                }

                backgroundResultRecorder.reset();
            }

            if (total == 0) {
                throw new RuntimeException("KeyMatch strategy expects at least one column marked as matchingRole=\"key\". Key must be unique in expected table");
            }

            if (total == success) {
                return row;
            }
        }
        return null;
    }
}
