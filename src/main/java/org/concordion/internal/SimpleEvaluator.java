package org.concordion.internal;

public class SimpleEvaluator extends OgnlEvaluator {

    public SimpleEvaluator(Object fixture) {
        super(fixture);
    }

    @Override
    public void setVariable(String expression, Object value) {
        if (!expression.trim().startsWith("#")) {
            throw new RuntimeException("Invalid 'set' expression [" + expression + "]");
        }
        super.setVariable(expression, value);
    }
}
