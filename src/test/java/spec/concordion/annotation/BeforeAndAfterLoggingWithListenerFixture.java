package spec.concordion.annotation;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.extension.Extension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.extension.ExampleEventTestExtension;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class BeforeAndAfterLoggingWithListenerFixture {
    private static List<String> log = new ArrayList<String>(); 
    
    @Extension
    private ExampleEventTestExtension ext1 = new ExampleEventTestExtension().withLog("Extension 1", log);
    
    @Extension
    private ExampleEventTestExtension ext2 = new ExampleEventTestExtension().withLog("Extension 2", log);
    
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
    public void logAfterSpecification() {
        log("After specification");
    }
    
    public void log(String msg) {
        log.add(msg);
    }
    
    public static List<String> getLog() {
        return log;
    }
}
