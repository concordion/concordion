package org.concordion.internal;

import junit.framework.TestCase;

import org.concordion.api.SpecificationLocator;

public class ClassNameBasedSpecificationLocatorTest extends TestCase {

    public void testRemovesWordTestFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/org/concordion/internal/ClassNameBasedSpecificationLocator.html", locator.locateSpecification(this, "html").getPath());
    }

    public void testRemovesWordFixtureFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/spec/concordion/common/command/execute/ContinueAfterExceptions.html",
                locator.locateSpecification(new spec.concordion.common.command.execute.ContinueAfterExceptionsFixture(), "html").getPath());
    }

    public void testCanAppendDotXHTMLWhenConstructedWithXHTMLArgument() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator("xhtml");
        assertEquals("/org/concordion/internal/ClassNameBasedSpecificationLocator.xhtml", locator.locateSpecification(this, "xhtml").getPath());
    }
}
