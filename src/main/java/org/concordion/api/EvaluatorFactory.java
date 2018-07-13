package org.concordion.api;

public interface EvaluatorFactory {

    Evaluator createEvaluator(Fixture fixture);
}
