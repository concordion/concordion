package spec.concordion.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.concordion.api.*;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

@RunWith(ConcordionRunner.class)
public class BeforeAndAfterLoggingFixture {
    private static List<String> log = new ArrayList<String>();

    @Rule
    public TestLogger logger = new TestLogger();

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

    public static void log(String msg) {
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

    public class TestLogger implements TestRule {
        @Override
        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    base.evaluate();
                }
            };
        }
    }

    @ClassRule
    public static final ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
        };

        @Override
        protected void after() {
        };
    };
}
