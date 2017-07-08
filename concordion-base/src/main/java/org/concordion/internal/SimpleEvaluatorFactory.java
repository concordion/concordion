package org.concordion.internal;

import org.concordion.api.Evaluator;
import org.concordion.api.EvaluatorFactory;

public class SimpleEvaluatorFactory implements EvaluatorFactory {

    public Evaluator createEvaluator(Object fixture) {
        return new SimpleEvaluator(fixture);
    }
}
