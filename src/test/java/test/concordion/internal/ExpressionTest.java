package test.concordion.internal;

import org.concordion.internal.SimpleEvaluator;

import junit.framework.TestCase;

public class ExpressionTest extends TestCase {
    
    private static String [] evaluationExpressions = new String [] { 
          "myProp"
        , "myMethod()"
        , "myMethod(#var1)"
        , "myMethod(#var1, #var2)"
        , "#var"
        , "#var.myProp"
        , "#var.myProp.myProp"
        , "#var = myProp"
        , "#var = myMethod()"
        , "#var = myMethod(#var1)"
        , "#var = myMethod(#var1, #var2)"
        , "#var ? 's1' : 's2'"
        , "myProp ? 's1' : 's2'"
        , "myMethod() ? 's1' : 's2'"
        , "myMethod(#var1) ? 's1' : 's2'"
        , "myMethod(#var1, #var2) ? 's1' : 's2'"
        , "#var.myMethod()"
        , "#var.myMethod(#var1)"
        , "#var.myMethod(#var1, #var2)"};
    
    public void testWhitespaceIsIgnoredInEvaluationExpressions() throws Exception {
        assertValidEvaluationExpression("myMethod(#var1,   #var2)");
        assertValidEvaluationExpression("  #var   =    myMethod   (    #var1,   #var2    )   ");
        assertValidEvaluationExpression("#var=myMethod(#var1,#var2)");
    }

    public void testWhitespaceIsIgnoredInSetVariableExpressions() throws Exception {
        assertValidSetVariableExpression("  #var   =    myMethod   (    #var1,   #var2    )   ");
        assertValidSetVariableExpression("  #var   =    myProp");
        assertValidSetVariableExpression("#var=myProp");
        assertValidSetVariableExpression("#var=myMethod(#var1,#var2)");
    }
    
    public void testAllEvaluationExpressions() throws Exception {
        for (String expr : evaluationExpressions) {
            try {
                assertValidEvaluationExpression(expr);
            } catch (Exception e) {
                fail("Expression incorrectly declared invalid: " + expr);
            }
        }
    }

    private void assertValidEvaluationExpression(String expression) {
        try {
            SimpleEvaluator.validateEvaluationExpression(expression);
        } catch (Exception e) {
            fail("Expression incorrectly declared invalid: " + expression);
        }
    }

    private void assertValidSetVariableExpression(String expression) {
        try {
            SimpleEvaluator.validateSetVariableExpression(expression);
        } catch (Exception e) {
            fail("Expression incorrectly declared invalid: " + expression);
        }
    }
}
