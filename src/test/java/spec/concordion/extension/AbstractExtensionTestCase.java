package spec.concordion.extension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.concordion.api.Resource;
import org.concordion.api.SpecificationScoped;
import org.concordion.api.extension.ConcordionExtension;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

public abstract class AbstractExtensionTestCase {

    private List<String> eventList;
    private TestRig testRig;
    private ProcessingResult processingResult;
    private ConcordionExtension extension;

    @SpecificationScoped
    private ExtensionTestHelper helper;

    public void processAnything() throws Exception { 
        process("<p>anything..</p>");
    }
    
    public void process(String fragment) throws Exception {
        testRig = new TestRig();
        configureTestRig(testRig);
        processingResult = testRig.withFixture(this)
          .withExtension(extension)
          .processFragment(fragment);
    }

    protected void configureTestRig(TestRig testRig) {
    }

    public PrintStream getLogStream() {
        return helper.getLogStream();
    }

    public List<String> getEventLog() {
        helper.getLogStream().flush();
        String[] events = helper.getBaos().toString().split("\\r?\\n");
        eventList = new ArrayList<String>(Arrays.asList(events));
        eventList.remove("");
        helper.getBaos().reset();
        return eventList;
    }

    public boolean isAvailable(String resourcePath) {
        return testRig.hasCopiedResource(new Resource(resourcePath));
    }

    protected ProcessingResult getProcessingResult() {
        return processingResult;
    }
    
    protected void setExtension(ConcordionExtension extension) {
        this.extension = extension;
    }
}