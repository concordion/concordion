package org.concordion.internal;

import ognl.MethodFailedException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.concordion.api.Evaluator;
import org.concordion.internal.util.Check;

public class OgnlEvaluator implements Evaluator {

    private Object rootObject;
    private final OgnlContext ognlContext = new OgnlContext();


    public OgnlEvaluator() {
    }

    public OgnlEvaluator(Object rootObject) {
        this.rootObject = rootObject;
    }

    public void setRootObject(Object rootObject) {
        this.rootObject = rootObject;
    }

    public Object evaluate(String expression) {
        Check.notNull(rootObject, "Root object is null");
        Check.notNull(expression, "Expression to evaluate cannot be null");
        try {
            return Ognl.getValue(expression, ognlContext, rootObject);
        } catch (OgnlException e) {
            throw invalidExpressionException(e);
        }
    }

    private InvalidExpressionException invalidExpressionException(OgnlException e) {
        Throwable cause = e;
        
        String message = e.getMessage();
        if (e.getReason() != null) {
            message = e.getReason().getMessage();
            cause = e.getReason();
        }
        if (message == null) {
            message = "";
        }
        if (e instanceof MethodFailedException) {
            MethodFailedException ex = ((MethodFailedException) e);
            Throwable realReason = ex.getReason();
            if (realReason != null) {
                if (realReason instanceof NullPointerException) {
                    message = "NullPointerException";
                } else {
                    message = realReason.getClass().getName() + ": " + message;
                }
            }
            message = message.replaceAll("java\\.lang\\.", "");
        }
        return new InvalidExpressionException(message, cause);
    }

    public void setVariable(String expression, Object value) {
        assertStartsWithHash(expression);
        if (expression.contains("=")) {
            evaluate(expression);
        } else {
            String rawVariable = expression.substring(1);
            putVariable(rawVariable, value);
        }
    }

    private void assertStartsWithHash(String expression) {
        if (!expression.startsWith("#")) {
            throw new InvalidExpressionException("Variable for concordion:set must start"
                    + " with '#'\n (i.e. change concordion:set=\"" + expression + "\" to concordion:set=\"#" + expression + "\".");
        }
    }

    private void putVariable(String rawVariableName, Object value) {
        Check.isFalse(rawVariableName.startsWith("#"), "Variable name passed to evaluator should not start with #");
        Check.isTrue(!rawVariableName.equals("in"), "'%s' is a reserved word and cannot be used for variables names", rawVariableName);
        ognlContext.put(rawVariableName, value);
    }

    public Object getVariable(String variableName) {
        assertStartsWithHash(variableName);
        String rawVariableName = variableName.substring(1);
        return ognlContext.get(rawVariableName);
    }
}
