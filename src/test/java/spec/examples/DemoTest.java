package spec.examples;

public class DemoTest extends DemoBase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
