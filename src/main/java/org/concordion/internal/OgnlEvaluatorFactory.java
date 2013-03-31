package org.concordion.internal;

import org.concordion.api.Evaluator;
import org.concordion.api.EvaluatorFactory;

public class OgnlEvaluatorFactory implements EvaluatorFactory {

    public Evaluator createEvaluator(Object fixture) {
        return new OgnlEvaluator(fixture);
    }
}
