package spec.concordion.annotation;

import org.concordion.api.Resources;
import org.concordion.api.Resources.InsertType;

@Resources(value = { "*.css", "demo.txt" }, insertType = InsertType.LINKED)
public class Demo extends DemoParent {
    
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
