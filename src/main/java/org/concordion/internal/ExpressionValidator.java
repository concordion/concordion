package org.concordion.internal;

public interface ExpressionValidator {

    void validate(String expression) throws InvalidExpressionException;
}
