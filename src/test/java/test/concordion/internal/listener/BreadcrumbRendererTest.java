package test.concordion.internal.listener;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.concordion.api.Resource;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.internal.XMLParser;
import org.concordion.internal.listener.BreadcrumbRenderer;
import org.junit.Rule;
import org.junit.Test;

import test.concordion.ConsoleLogGobbler;
import test.concordion.StubLogger;
import test.concordion.StubSource;

public class BreadcrumbRendererTest {

    private static final String EXPECTED_SOURCE_NAME = "stub";
    private static final String SPEC_RESOURCE_NAME = "/parent/Child.html";
    private static final String PACKAGE_RESOURCE_NAME = "/parent/Parent.html";
    private static final String ERRONEOUS_HTML = "<html><head></html>";
    
    private StubSource stubSource = new StubSource();
    private StubLogger stubLogger = new StubLogger();
    private BreadcrumbRenderer renderer = new BreadcrumbRenderer(stubSource, new XMLParser());
    
    @Rule 
    public ConsoleLogGobbler logGobbler = new ConsoleLogGobbler();  // Ensure error log messages don't appear on console

    @Test
    public void logsNameOfErroneousHtmlFileOnParseError() {
        stubSource.addResource(PACKAGE_RESOURCE_NAME, ERRONEOUS_HTML);
        SpecificationProcessingEvent event = new SpecificationProcessingEvent(new Resource(SPEC_RESOURCE_NAME), null);
        renderer.afterProcessingSpecification(event);

        String logMessage = stubLogger.getNewLogMessages();
        assertThat(logMessage, containsString("Failed to parse XML document"));
        assertThat(logMessage, containsString(String.format("[%s: %s]", EXPECTED_SOURCE_NAME, PACKAGE_RESOURCE_NAME)));
    }
}
