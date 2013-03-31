package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

public class SimpleEvaluator extends OgnlEvaluator {

    public SimpleEvaluator(Object fixture) {
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

    private static String METHOD_NAME_PATTERN = "[a-z][a-zA-Z0-9_]*";
    private static String PROPERTY_NAME_PATTERN = "[a-z][a-zA-Z0-9_]*";
    private static String STRING_PATTERN = "'[^']+'";
    private static String LHS_VARIABLE_PATTERN = "#" + METHOD_NAME_PATTERN;
    private static String RHS_VARIABLE_PATTERN = "(" + LHS_VARIABLE_PATTERN + "|#TEXT|#HREF)";
    
    public static void validateEvaluationExpression(String expression) {
        
        // Examples of possible expressions in test.concordion.internal.ExpressionTest
        
        String METHOD_CALL_PARAMS = METHOD_NAME_PATTERN + " *\\( *" + RHS_VARIABLE_PATTERN + "(, *" + RHS_VARIABLE_PATTERN + " *)*\\)";
        String METHOD_CALL_NO_PARAMS = METHOD_NAME_PATTERN + " *\\( *\\)";
        String TERNARY_STRING_RESULT = " \\? " + STRING_PATTERN + " : " + STRING_PATTERN;
        
        List<String> regexs = new ArrayList<String>();
        regexs.add(PROPERTY_NAME_PATTERN);
        regexs.add(METHOD_CALL_NO_PARAMS);
        regexs.add(METHOD_CALL_PARAMS);
        regexs.add(RHS_VARIABLE_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + "(\\." + PROPERTY_NAME_PATTERN +  ")+");
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_CALL_NO_PARAMS);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_CALL_PARAMS);
        regexs.add(LHS_VARIABLE_PATTERN + TERNARY_STRING_RESULT);
        regexs.add(PROPERTY_NAME_PATTERN + TERNARY_STRING_RESULT);
        regexs.add(METHOD_CALL_NO_PARAMS + TERNARY_STRING_RESULT);
        regexs.add(METHOD_CALL_PARAMS + TERNARY_STRING_RESULT);
        regexs.add(LHS_VARIABLE_PATTERN + "\\." + METHOD_CALL_NO_PARAMS);
        regexs.add(LHS_VARIABLE_PATTERN + "\\." + METHOD_CALL_PARAMS);
        
        expression = expression.trim();
        for (String regex : regexs) {
            if (expression.matches(regex)) {
                return;
            }
        }
        throw new RuntimeException("Invalid expression [" + expression + "]");
    }

    public static void validateSetVariableExpression(String expression) {
        // #var                         VARIABLE
        // #var = myProp                VARIABLE = PROPERTY
        // #var = myMethod()            VARIABLE = METHOD
        // #var = myMethod(var1)        VARIABLE = METHOD_WITH_PARAM
        // #var = myMethod(var1, var2)  VARIABLE = METHOD_WITH_MULTIPLE_PARAMS
        
        List<String> regexs = new ArrayList<String>();
        regexs.add(RHS_VARIABLE_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + "\\." + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_NAME_PATTERN + " *\\( *\\)");
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_NAME_PATTERN + " *\\( *" + RHS_VARIABLE_PATTERN + "(, *" + RHS_VARIABLE_PATTERN + " *)*\\)");
        
        expression = expression.trim();
        for (String regex : regexs) {
            if (expression.matches(regex)) {
                return;
            }
        }
        throw new RuntimeException("Invalid 'set' expression [" + expression + "]");
    }
}
