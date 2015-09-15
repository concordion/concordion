package spec.concordion.command;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import java.util.Map;

import static java.util.Collections.singletonMap;

@RunWith(ConcordionRunner.class)
public class PossibleOgnExpressionsTest {

    public String property = "property";
    public String[] arrayProperty = new String[] {"arrayProperty[0]"};
    public Map<String, String> mapProperty = singletonMap("value", "mapProperty['value']");
    public Complex complexProperty = new Complex()
            .withSubProperty("complexProperty.subProperty")
            .withSubMethod("complexProperty.subMethod()");
    public ComplexChain complexChainPropertyViaProperty = new ComplexChain().withNext(new Complex()
            .withSubProperty("complexChainPropertyViaProperty.next.subProperty")
            .withSubMethod("complexChainPropertyViaProperty.next.subMethod()"));
    public ComplexChain complexChainPropertyViaMethod = new ComplexChain().withNext(new Complex()
            .withSubProperty("complexChainPropertyViaMethod.next().subProperty")
            .withSubMethod("complexChainPropertyViaMethod.next().subMethod()"));

    public String method() {return "method()";}
    public String methodWithArguments(String argument) {return argument;}
    public String[] methodReturningArray() {return new String[] {"methodReturningArray()[0]"};}
    public Map<String, String> methodReturningMap() {return singletonMap("value", "methodReturningMap()['value']");}
    public Complex methodReturningComplexType() {return new Complex()
            .withSubProperty("methodReturningComplexType().subProperty")
            .withSubMethod("methodReturningComplexType().subMethod()");}
    public ComplexChain methodReturningComplexChainViaProperty() {return new ComplexChain().withNext(new Complex()
            .withSubProperty("methodReturningComplexChainViaProperty().next.subProperty")
            .withSubMethod("methodReturningComplexChainViaProperty().next.subMethod()"));}
    public ComplexChain methodReturningComplexChainViaMethod() {return new ComplexChain().withNext(new Complex()
            .withSubProperty("methodReturningComplexChainViaMethod().next().subProperty")
            .withSubMethod("methodReturningComplexChainViaMethod().next().subMethod()"));}
    
    public String createVariable() {return "#variable";}
    public String[] createArrayVariable() {return new String[] {"#arrayVariable[0]"};}
    public Map<String, String> createMapVariable() {return singletonMap("value", "#mapVariable['value']");}
    public Complex createComplexVariable() {return new Complex()
            .withSubProperty("#complexVariable.subProperty")
            .withSubMethod("#complexVariable.subMethod()");}
    public ComplexChain createComplexChainViaPropertyVariable() {return new ComplexChain().withNext(new Complex()
            .withSubProperty("#complexChainViaPropertyVariable.next.subProperty")
            .withSubMethod("#complexChainViaPropertyVariable.next.subMethod()"));}
    public ComplexChain createComplexChainViaMethodVariable() {return new ComplexChain().withNext(new Complex()
            .withSubProperty("#complexChainViaMethodVariable.next().subProperty")
            .withSubMethod("#complexChainViaMethodVariable.next().subMethod()"));}

    public static final class Complex {

        public String subMethod;
        public String subProperty;

        public String subMethod() {
            return subMethod;
        }

        public Complex withSubProperty(String subProperty) {
            this.subProperty = subProperty;
            return this;
        }

        public Complex withSubMethod(String subMethod) {
            this.subMethod = subMethod;
            return this;
        }
    }

    public static final class ComplexChain {

        public Complex next;

        public Complex next() {
            return next;
        }

        public ComplexChain withNext(Complex next) {
            this.next = next;
            return this;
        }
    }

    public PropertyCreator createMixedExample() {
        return new PropertyCreator();
    }

    private static final class PropertyCreator {
        public MyProperties createProperties(String appName) {
            MyProperties myProperties = new MyProperties();
            myProperties.map = singletonMap("system_file", new String[]{"#var.createProperties('app').map['system_file'][0]"});
            return myProperties;
        }
    }

    private static final class MyProperties {

        public Map<String, String[]> map;
    }

}
