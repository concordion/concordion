package test.concordion.internal;

import org.concordion.api.SpecificationLocator;
import org.concordion.internal.ClassNameBasedSpecificationLocator;
import junit.framework.TestCase;

public class ClassNameBasedSpecificationLocatorTest extends TestCase {

    private SpecificationLocator locator = new ClassNameBasedSpecificationLocator();

    public void testRemovesWordTestFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        assertEquals("/test/concordion/internal/ClassNameBasedSpecificationLocator.html", locator.locateSpecification(this).getPath());
    }
}
