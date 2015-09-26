package org.concordion.api;

public interface EvaluatorFactory {

    Evaluator createEvaluator(Object fixture);
}
