package spec.concordion.command.expressions;

import org.concordion.api.Evaluator;
import org.concordion.api.FullOGNL;
import org.concordion.api.MultiValueResult;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.OgnlEvaluator;
import org.concordion.internal.SimpleEvaluator;
import org.junit.runner.RunWith;

import java.util.Map;

import static java.util.Collections.singletonMap;

@RunWith(ConcordionRunner.class)
public class ComplexExpressionsTest {

    private final Evaluator simple = new SimpleEvaluator(new SimpleTestFixture());
    private final Evaluator ognl = new OgnlEvaluator(new OgnlTestFixture());

    public ComplexExpressionsTest() {
        initTestVariables(simple);
        initTestVariables(ognl);
    }

    public Object checkExpression(String expression) {
        return new MultiValueResult()
                .with("simple", toTableText(isExpressionSupportedBy(simple, expression)))
                .with("ognl", toTableText(isExpressionSupportedBy(ognl, expression)));
    }

    private boolean isExpressionSupportedBy(Evaluator evaluator, String expression) {
        try {
            evaluator.evaluate(expression);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private String toTableText(boolean val) {
        return val ? "yes" : "no";
    }

    private void initTestVariables(Evaluator evaluator) {
        evaluator.setVariable("#arg1", "#arg1");
        evaluator.setVariable("#arg2", "#arg2");

        evaluator.setVariable("#variable", "variable");
        evaluator.setVariable("#arrayVariable", new String[]{"arrayVariable[0]"});
        evaluator.setVariable("#mapVariable", singletonMap("key", "#mapVariable['key']"));
        evaluator.setVariable("#complexVariable", new Complex());
        evaluator.setVariable("#chainVariable", new Chain());
    }

    public static class SimpleTestFixture {

        public String property = "property";
        public String[] arrayProperty = new String[] {"arrayProperty[0]"};
        public Map<String, String> mapProperty = singletonMap("key", "mapProperty['key']");
        public Complex complexProperty = new Complex();
        public Chain chainProperty = new Chain();

        public String method() {return "method()";}
        public String methodWithArg(String arg1) {return "methodWithArg(arg1)";}
        public String methodWithArgs(String arg1, String arg2) {return "methodWithArgs(arg1,arg2)";}
        public String[] methodReturningArray() {return new String[] {"arrayReturn[0]"};}
        public Map<String, String> methodReturningMap() {return singletonMap("key", "mapReturn['key']");}
        public Complex methodReturningComplex() {return new Complex();}
        public Chain methodReturningChain() {return new Chain();}
    }

    @FullOGNL
    public static class OgnlTestFixture extends SimpleTestFixture {

    }

    public static final class Complex {

        public String nestedProperty;

        public String nestedMethod() {return "nestedMethod()";}
        public String nestedMethodWithArg(String arg) {return "nestedMethodWithArg(arg1)";}
        public String nestedMethodWithArgs(String arg1, String arg2) {return "nestedMethodWithArgs(arg1,arg2)";}
    }

    public static final class Chain {

        public Complex chain = new Complex();

        public Complex chain() {return new Complex();}
        public Complex chainWithArg(String arg) {return new Complex();}
        public Complex chainWithArgs(String arg1, String arg2) {return new Complex();}
    }
}
