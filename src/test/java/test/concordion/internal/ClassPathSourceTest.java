package test.concordion.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.concordion.api.Resource;
import org.concordion.internal.ClassPathSource;
import org.concordion.internal.util.IOUtil;
import org.junit.Test;

public class ClassPathSourceTest {

    private static final String EXPECTED_SOURCE_NAME = "classpath";
    private static final String MISSING_RESOURCE_NAME = "/NonExistent.html";
    
    private ClassPathSource source = new ClassPathSource(new IOUtil());
    
    @Test
    public void failsWithIOExceptionOnCreateInputStreamWithMissingResource() throws Exception {
        try {
            source.createInputStream(new Resource(MISSING_RESOURCE_NAME));
            fail("Expected IOException");
        } catch (IOException e) {
            assertThat(e.getMessage(), containsString("not found"));
            assertThat(e.getMessage(), containsString(String.format("[%s: %s]", EXPECTED_SOURCE_NAME, MISSING_RESOURCE_NAME)));
        }
    }
}
