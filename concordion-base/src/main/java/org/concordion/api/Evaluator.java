package org.concordion.api;

public interface Evaluator {

    Object getVariable(String variableName);

    void setVariable(String variableName, Object value);

    Object evaluate(String expression);
}
