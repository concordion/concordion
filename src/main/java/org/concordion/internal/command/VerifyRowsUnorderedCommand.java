package org.concordion.internal.command;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;
import org.concordion.internal.util.Check;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyRowsUnorderedCommand extends AbstractVerifyRowsCommand {

    @SuppressWarnings("unchecked")
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Pattern pattern = Pattern.compile("(#.+?) *: *(.+)");
        Matcher matcher = pattern.matcher(commandCall.getExpression());
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"verifyRowsUnordered\" should be of the form: #var : collectionExpr");
        }
        String loopVariableName = matcher.group(1);
        String iterableExpression = matcher.group(2);

        Object obj = evaluator.evaluate(iterableExpression);
        Check.notNull(obj, "Expression returned null (should be an Iterable).");
        Check.isTrue(obj instanceof Iterable, obj.getClass().getCanonicalName() + " is not Iterable");
        Iterable<Object> iterable = (Iterable<Object>) obj;

        List<Object> evaluationResults = new ArrayList<Object>();
        for (Object o : iterable) {
            evaluationResults.add(o);
        }

        TableSupport tableSupport = new TableSupport(commandCall);
        Row[] detailRows = tableSupport.getDetailRows();

        announceExpressionEvaluated(commandCall.getElement());

        for (Row detailRow : detailRows) {
            tableSupport.copyCommandCallsTo(detailRow);
            boolean found = false;
            for (Object evaluationResult : evaluationResults) {
                evaluator.setVariable(loopVariableName, evaluationResult);
                if (commandCall.getChildren().verifyInBackground(evaluator, resultRecorder) == Result.SUCCESS) {
                    commandCall.getChildren().verify(evaluator, resultRecorder);
                    evaluationResults.remove(evaluationResult);
                    found = true;
                    break;
                }
            }
            if (!found) {
                announceMissingRow(detailRow.getElement());
            }
        }

        for (Object evaluationResult : evaluationResults) {
            evaluator.setVariable(loopVariableName, evaluationResult);
            Row detailRow = tableSupport.addDetailRow();
            announceSurplusRow(detailRow.getElement());
            tableSupport.copyCommandCallsTo(detailRow);
            commandCall.getChildren().verify(evaluator, resultRecorder);
        }
    }
}
