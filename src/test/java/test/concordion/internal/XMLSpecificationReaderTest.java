package test.concordion.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.concordion.api.Resource;
import org.concordion.internal.ParsingException;
import org.concordion.internal.XMLParser;
import org.concordion.internal.XMLSpecificationReader;
import org.junit.Test;

import test.concordion.StubSource;

public class XMLSpecificationReaderTest {

    private static final String EXPECTED_SOURCE_NAME = "stub";
    private static final String SPEC_RESOURCE_NAME = "/Spec.html";
    private static final String ERRONEOUS_HTML = "<html><head></html>";
    
    private StubSource stubSource = new StubSource();
    private XMLSpecificationReader reader = new XMLSpecificationReader(stubSource, new XMLParser(), null);
    
    @Test
    public void includesSourceAndResourceNameOnFailure() throws Exception {
        try {
            stubSource.addResource(SPEC_RESOURCE_NAME, ERRONEOUS_HTML);
            reader.readSpecification(new Resource(SPEC_RESOURCE_NAME));
            fail("Expected ParsingException");
        } catch (ParsingException e) {
            assertThat(e.getMessage(), containsString("Failed to parse XML document"));
            assertThat(e.getMessage(), containsString(String.format("[%s: %s]", EXPECTED_SOURCE_NAME, SPEC_RESOURCE_NAME)));
        }
    }
}
