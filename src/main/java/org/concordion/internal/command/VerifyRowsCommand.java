package org.concordion.internal.command;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;
import org.concordion.internal.util.Check;

public class VerifyRowsCommand extends AbstractVerifyRowsCommand {

    @SuppressWarnings("unchecked")
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Matcher matcher = COMMAND_PATTERN.matcher(commandCall.getExpression());
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"verifyRows\" should be of the form: #var : collectionExpr");
        }
        String loopVariableName = matcher.group(1);
        String iterableExpression = matcher.group(2);

        Object obj = evaluator.evaluate(iterableExpression);
        Check.notNull(obj, "Expression returned null (should be an Iterable).");
        Check.isTrue(obj instanceof Iterable, obj.getClass().getCanonicalName() + " is not Iterable");
        Check.isTrue(!(obj instanceof HashSet) || (obj instanceof LinkedHashSet), obj.getClass().getCanonicalName() + " does not have a predictable iteration order");
        Iterable<Object> iterable = (Iterable<Object>) obj;
        
        TableSupport tableSupport = new TableSupport(commandCall);
        Row[] detailRows = tableSupport.getDetailRows();

        announceExpressionEvaluated(commandCall.getElement());
        
        int index = 0;
        for (Object loopVar : iterable) {
            evaluator.setVariable(loopVariableName, loopVar);
            Row detailRow;
            if (detailRows.length > index) {
                detailRow = detailRows[index];
            } else {
                detailRow = tableSupport.addDetailRow();
                announceSurplusRow(detailRow.getElement());
            }
            tableSupport.copyCommandCallsTo(detailRow);
            commandCall.getChildren().verify(evaluator, resultRecorder);
            index++;
        }
        
        for (; index < detailRows.length; index++) {
            Row detailRow = detailRows[index];
            resultRecorder.record(Result.FAILURE);
            announceMissingRow(detailRow.getElement());
        }
    }
}
