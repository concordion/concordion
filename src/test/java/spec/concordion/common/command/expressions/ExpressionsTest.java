package spec.concordion.common.command.expressions;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.SimpleEvaluator;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class ExpressionsTest {

    public boolean isValidEvaluationExpression(String evaluateExpression) {
        try {
            SimpleEvaluator.validateEvaluationExpression(evaluateExpression);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isValidSetVariableExpression(String setVariableExpression) {
        try {
            SimpleEvaluator.validateEvaluationExpression(setVariableExpression);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
