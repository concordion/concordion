package test.concordion.internal;

import org.concordion.internal.SimpleEvaluator;

import junit.framework.TestCase;

public class SimpleEvaluatorTest extends TestCase {

    private SimpleEvaluator evaluator = new SimpleEvaluator(this);
    
    public void testSupportsTemporaryVariables() throws Exception {
        evaluator.setVariable("#temp", "Fred");
        assertEquals("Fred", evaluator.evaluate("#temp"));
    }
    
    public void testVariablesCanBeOverwritten() {
        evaluator.setVariable("#temp", "Fred");
        evaluator.setVariable("#temp", "Barney");
        assertEquals("Barney", evaluator.evaluate("#temp"));
    }

    public void testVariablesCanBePassedToMethods() {
        evaluator.setVariable("#temp", "Fred");
        assertEquals("Fred", evaluator.evaluate("myMethod(#temp)"));
    }
    
    public String myMethod(String s) {
        return s;
    }
    
    public void testVariablesAreCoercedToFitMethodParameters() throws Exception {
        evaluator.setVariable("#age", "15");
        assertEquals(15L, evaluator.evaluate("myLongMethod(#age)"));
        evaluator.setVariable("#age", "12.34");
        assertEquals(12.34d, evaluator.evaluate("myDoubleMethod(#age)"));
    }
    
    public long myLongMethod(long value) {
        return value;
    }
    
    public double myDoubleMethod(double value) {
        return value;
    }
    
    public int square(int x) {
        return x * x;
    }
    
    public Outer getOuter() {
        return new Outer();
    }
    
    public class Outer {
        private Inner inner;
        public Outer() {
            inner = new Inner();
        }
        public Inner getInner() {
            return inner;
        }
        public class Inner {
            public int getValue() {
                return 1;
            }
        }
    }
    
    public void testAccessInnerVariable() {
        evaluator.setVariable("#result", this);
        assertEquals(1, evaluator.evaluate("#result.outer.inner.value"));
    }

    public void testEvaluationsCanCreateVariables() throws Exception {
        evaluator.setVariable("#x", "10");
        evaluator.evaluate("#y = square(#x)");
        assertEquals(100, evaluator.evaluate("#y"));
        assertEquals(100, evaluator.getVariable("#y"));
    }
}
