package test.concordion;

import org.concordion.api.Evaluator;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Fixture;

public class StubEvaluator implements Evaluator, EvaluatorFactory {

    private Object evaluationResult = null;

    public Evaluator createEvaluator(Fixture fixture) {
        return this;
    }

    public Object evaluate(String expression) {
        if (evaluationResult instanceof RuntimeException) {
            throw (RuntimeException) evaluationResult;
        }
        return evaluationResult;
    }

    public Object getVariable(String variableName) {
        return null;
    }

    public void setVariable(String variableName, Object value) {
    }

    public EvaluatorFactory withStubbedResult(Object evaluationResult) {
        this.evaluationResult = evaluationResult;
        return this;
    }

}
