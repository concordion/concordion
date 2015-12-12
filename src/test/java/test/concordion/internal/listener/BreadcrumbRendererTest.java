package test.concordion.internal.listener;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.concordion.api.Resource;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.internal.SpecificationType;
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
    private static final String BAD_SPEC_RESOURCE_NAME = "/badparent/Child.html";
    private static final String BAD_PACKAGE_RESOURCE_NAME = "/badparent/Badparent.html";
    private static final String ERRONEOUS_HTML = "<html><head></html>";
    private static final String GOOD_HTML = "<html><head></head><body></body></html>";
    
    private StubSource stubSource = new StubSource();
    private StubLogger stubLogger = new StubLogger();
    private BreadcrumbRenderer renderer;
    
    @Rule 
    public ConsoleLogGobbler logGobbler = new ConsoleLogGobbler();  // Ensure error log messages don't appear on console

    public BreadcrumbRendererTest() {
        List<SpecificationType> specificationTypes = new ArrayList<SpecificationType>();
        specificationTypes.add(new SpecificationType("html", null));
        renderer  = new BreadcrumbRenderer(stubSource, new XMLParser(), specificationTypes);
    }

    @Test
    public void logsNameOfErroneousHtmlFileOnParseError() {
        stubSource.addResource(BAD_PACKAGE_RESOURCE_NAME, ERRONEOUS_HTML);
        SpecificationProcessingEvent event = new SpecificationProcessingEvent(new Resource(BAD_SPEC_RESOURCE_NAME), null);
        renderer.afterProcessingSpecification(event);

        String logMessage = stubLogger.getNewLogMessages();
        assertThat(logMessage, containsString("Failed to parse XML document"));
        assertThat(logMessage, containsString(String.format("[%s: %s]", EXPECTED_SOURCE_NAME, BAD_PACKAGE_RESOURCE_NAME)));
    }

    @Test
    public void createsBodyIfNoneExists() {
        stubSource.addResource(PACKAGE_RESOURCE_NAME, GOOD_HTML);
        Element rootElement = new Element("html");
        SpecificationProcessingEvent event = new SpecificationProcessingEvent(new Resource(SPEC_RESOURCE_NAME), new org.concordion.api.Element(rootElement));
        renderer.afterProcessingSpecification(event);

        String logMessage = stubLogger.getNewLogMessages();
        assertThat(logMessage, is(""));
        assertThat(rootElement.getChildElements("body").size(), is(1));
    }

    @Test
    public void createsSpanWithParentBreadcrumb() {
        stubSource.addResource(PACKAGE_RESOURCE_NAME, GOOD_HTML);
        Element rootElement = new Element("html");
        SpecificationProcessingEvent event = new SpecificationProcessingEvent(new Resource(SPEC_RESOURCE_NAME), new org.concordion.api.Element(rootElement));
        renderer.afterProcessingSpecification(event);

        Elements bodySpans = rootElement.getChildElements("body").get(0).getChildElements("span");
        assertThat(bodySpans.size(), is(1));
        assertThat(bodySpans.get(0).getAttributeValue("class"), is("breadcrumbs"));
        assertThat(bodySpans.get(0).getChildElements("a").size(), is(1));
        assertThat(bodySpans.get(0).getChildElements("a").get(0).getAttributeValue("href"), is("Parent.html"));
        assertThat(bodySpans.get(0).getChildElements("a").get(0).getValue(), is("Parent"));
    }

    @Test
    public void cachesResult() {
        stubSource.addResource(PACKAGE_RESOURCE_NAME, GOOD_HTML);
        Element rootElement = new Element("html");
        SpecificationProcessingEvent event = new SpecificationProcessingEvent(new Resource(SPEC_RESOURCE_NAME), new org.concordion.api.Element(rootElement));
        renderer.afterProcessingSpecification(event);

        Elements bodySpans = rootElement.getChildElements("body").get(0).getChildElements("span");
        assertThat(bodySpans.get(0).getChildElements("a").get(0).getValue(), is("Parent"));

        // Changing the package resource to bad HTML has no effect since we cached the first result
        stubSource.addResource(PACKAGE_RESOURCE_NAME, ERRONEOUS_HTML);
        rootElement = new Element("html");
        event = new SpecificationProcessingEvent(new Resource(SPEC_RESOURCE_NAME), new org.concordion.api.Element(rootElement));
        renderer.afterProcessingSpecification(event);

        bodySpans = rootElement.getChildElements("body").get(0).getChildElements("span");
        assertThat(bodySpans.get(0).getChildElements("a").get(0).getValue(), is("Parent"));
    }
}
