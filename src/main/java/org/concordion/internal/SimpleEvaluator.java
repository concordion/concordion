package org.concordion.internal;

import org.concordion.api.Fixture;

public class SimpleEvaluator extends OgnlEvaluator {

    public SimpleEvaluator(Fixture fixture) {
        super(fixture);
    }

    @Override
    public Object evaluate(String expression) {
        validateEvaluationExpression(expression);
        return super.evaluate(expression);
    }

    @Override
    public void setVariable(String expression, Object value) {
        validateSetVariableExpression(expression);
        super.setVariable(expression, value);
    }


    public static void validateEvaluationExpression(String expression) {
        if (!EVALUATION_PATTERNS.matches(expression)) {
            throw new RuntimeException("Invalid expression [" + expression + "]");
        }
    }

    public static void validateSetVariableExpression(String expression) {
        if (!SET_VARIABLE_PATTERNS.matches(expression)) {
            throw new RuntimeException("Invalid 'set' expression [" + expression + "]");
        }
    }

    private static final String METHOD_NAME_PATTERN = "[a-z][a-zA-Z0-9_]*";
    private static final String PROPERTY_NAME_PATTERN = "[a-z][a-zA-Z0-9_]*";
    private static final String STRING_PATTERN = "'[^']+'";
    private static final String LHS_VARIABLE_PATTERN = "#" + METHOD_NAME_PATTERN;
    private static final String RHS_VARIABLE_PATTERN = "(" + LHS_VARIABLE_PATTERN + "|#TEXT|#HREF|#LEVEL)";
    private static final String METHOD_CALL_PARAMS = METHOD_NAME_PATTERN + " *\\( *" + RHS_VARIABLE_PATTERN + "(, *" + RHS_VARIABLE_PATTERN + " *)*\\)";
    private static final String METHOD_CALL_NO_PARAMS = METHOD_NAME_PATTERN + " *\\( *\\)";
    private static final String TERNARY_STRING_RESULT = " \\? " + STRING_PATTERN + " : " + STRING_PATTERN;

    private static final MultiPattern EVALUATION_PATTERNS = MultiPattern.fromRegularExpressions(
            PROPERTY_NAME_PATTERN,
            METHOD_CALL_NO_PARAMS,
            METHOD_CALL_PARAMS,
            RHS_VARIABLE_PATTERN,
            LHS_VARIABLE_PATTERN + "(\\." + PROPERTY_NAME_PATTERN + ")+",
            LHS_VARIABLE_PATTERN + " *= *" + PROPERTY_NAME_PATTERN,
            LHS_VARIABLE_PATTERN + " *= *" + METHOD_CALL_NO_PARAMS,
            LHS_VARIABLE_PATTERN + " *= *" + METHOD_CALL_PARAMS,
            LHS_VARIABLE_PATTERN + TERNARY_STRING_RESULT,
            PROPERTY_NAME_PATTERN + TERNARY_STRING_RESULT,
            METHOD_CALL_NO_PARAMS + TERNARY_STRING_RESULT,
            METHOD_CALL_PARAMS + TERNARY_STRING_RESULT,
            LHS_VARIABLE_PATTERN + "\\." + METHOD_CALL_NO_PARAMS,
            LHS_VARIABLE_PATTERN + "\\." + METHOD_CALL_PARAMS);

    // #var                         VARIABLE
    // #var = myProp                VARIABLE = PROPERTY
    // #var = myMethod()            VARIABLE = METHOD
    // #var = myMethod(var1)        VARIABLE = METHOD_WITH_PARAM
    // #var = myMethod(var1, var2)  VARIABLE = METHOD_WITH_MULTIPLE_PARAMS
    private static final MultiPattern SET_VARIABLE_PATTERNS = MultiPattern.fromRegularExpressions(
            RHS_VARIABLE_PATTERN,
            LHS_VARIABLE_PATTERN + "\\." + PROPERTY_NAME_PATTERN,
            LHS_VARIABLE_PATTERN + " *= *" + PROPERTY_NAME_PATTERN,
            LHS_VARIABLE_PATTERN + " *= *" + METHOD_NAME_PATTERN + " *\\( *\\)",
            LHS_VARIABLE_PATTERN + " *= *" + METHOD_NAME_PATTERN + " *\\( *" + RHS_VARIABLE_PATTERN + "(, *" + RHS_VARIABLE_PATTERN + " *)*\\)");
}
