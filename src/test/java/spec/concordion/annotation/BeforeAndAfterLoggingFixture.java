package spec.concordion.annotation;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class BeforeAndAfterLoggingFixture {
    private static List<String> log = new ArrayList<String>();

    @BeforeExample
    public void logBeforeExample(@ExampleName String exampleName) {
        log("Before " + exampleName);
    }

    @AfterExample
    public void logAfterExample(@ExampleName String exampleName) {
        log("After " + exampleName);
    }

    @BeforeSpecification
    public void logBeforeSpecification() {
        log("Before specification");
    }

    @AfterSpecification
    public void logAfterExample() {
        log("After specification");
    }

    public void log(String msg) {
        log.add(msg);
    }

    public static List<String> getLog() {
        return log;
    }

    public String error() {
        throw new RuntimeException("exception");
    }

    public String ok() {
        return "ok";
    }
}
