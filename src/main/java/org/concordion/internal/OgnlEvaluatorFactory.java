package org.concordion.internal;

import org.concordion.api.Evaluator;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Fixture;

public class OgnlEvaluatorFactory implements EvaluatorFactory {

    public Evaluator createEvaluator(Fixture fixture) {
        return new OgnlEvaluator(fixture);
    }
}
