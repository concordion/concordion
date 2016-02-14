package test.concordion.internal;

import junit.framework.TestCase;

import org.concordion.api.SpecificationLocator;
import org.concordion.internal.ClassNameBasedSpecificationLocator;

public class ClassNameBasedSpecificationLocatorTest extends TestCase {

    public void testRemovesWordTestFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/test/concordion/internal/ClassNameBasedSpecificationLocator.html", locator.locateSpecification(this, "html").getPath());
    }

    public void testRemovesWordFixtureFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/spec/concordion/command/execute/ContinueAfterExceptions.html", 
                locator.locateSpecification(new spec.concordion.command.execute.ContinueAfterExceptionsFixture(), "html").getPath());
    }

    public void testCanAppendDotXHTMLWhenConstructedWithXHTMLArgument() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator("xhtml");
        assertEquals("/test/concordion/internal/ClassNameBasedSpecificationLocator.xhtml", locator.locateSpecification(this, "xhtml").getPath());
    }
}
