package org.concordion.internal.command;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ElementUpdating;
import org.concordion.internal.Row;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.TableSupport;
import org.concordion.internal.util.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchRowsCommand extends AbstractRowsCommand {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("(#.+?) *: *(.+)");

    @SuppressWarnings("unchecked")
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Matcher matcher = COMMAND_PATTERN.matcher(commandCall.getExpression());
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"verifyRowsUnordered\" should be of the form: #var : collectionExpr");
        }
        String loopVariableName = matcher.group(1);
        String iterableExpression = matcher.group(2);

        Object actualData = evaluator.evaluate(iterableExpression);
        Check.notNull(actualData, "Expression returned null (should be an Iterable).");
        Check.isTrue(actualData instanceof Iterable, actualData.getClass().getCanonicalName() + " is not Iterable");

        new RowVerificationTable(commandCall, evaluator, resultRecorder, loopVariableName, (Iterable<Object>) actualData).verify();
    }

    private final class RowVerificationTable {

        private final CommandCall commandCall;
        private final Evaluator evaluator;
        private final ResultRecorder resultRecorder;
        private final String loopVariableName;
        private final TableSupport tableSupport;
        private final Row[] expectedRows;
        private final List<Object> actualRows;

        public RowVerificationTable(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, String loopVariableName, Iterable<Object> actualRows) {
            this.commandCall = commandCall;
            this.evaluator = evaluator;
            this.resultRecorder = resultRecorder;
            this.loopVariableName = loopVariableName;
            this.tableSupport = new TableSupport(commandCall);
            this.expectedRows = tableSupport.getDetailRows();
            this.actualRows = copy(actualRows);
        }

        public void verify() {
            announceExpressionEvaluated(commandCall.getElement());
            for (Row expectedRow : expectedRows) {
                tableSupport.copyCommandCallsTo(expectedRow);
                Object row = findRow();
                if (row != null) {
                    commandCall.getChildren().verify(evaluator, resultRecorder);
                    actualRows.remove(row);
                } else {
                    announceMissingRow(expectedRow.getElement());
                }
            }
            reportSurpulusRows();
        }

        private Object findRow() {
            try {
                ElementUpdating.instance().restrict();
                SummarizingResultRecorder backgroundResultRecorder = new SummarizingResultRecorder();
                for (Object row : actualRows) {
                    evaluator.setVariable(loopVariableName, row);
                    commandCall.getChildren().verify(evaluator, backgroundResultRecorder);
                    if (backgroundResultRecorder.isAllSuccessful()) {
                        return row;
                    }
                    backgroundResultRecorder.reset();
                }
                return null;
            } finally {
                ElementUpdating.instance().allow();
            }
        }

        private void reportSurpulusRows() {
            for (Object surpulusRow : actualRows) {
                evaluator.setVariable(loopVariableName, surpulusRow);
                Row detailRow = tableSupport.addDetailRow();
                announceSurplusRow(detailRow.getElement());
                tableSupport.copyCommandCallsTo(detailRow);
                commandCall.getChildren().verify(evaluator, resultRecorder);
            }
        }

        private List<Object> copy(Iterable<Object> iterable) {
            List<Object> copy = new ArrayList<Object>();
            for (Object o : iterable) {
                copy.add(o);
            }
            return copy;
        }
    }
}
