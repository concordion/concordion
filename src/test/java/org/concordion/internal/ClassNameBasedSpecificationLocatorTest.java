package org.concordion.internal;

import junit.framework.TestCase;

import org.concordion.api.SpecificationLocator;
import spec.concordion.common.command.execute.ContinueAfterExceptionsFixture;

public class ClassNameBasedSpecificationLocatorTest extends TestCase {

    public void testRemovesWordTestFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/org/concordion/internal/ClassNameBasedSpecificationLocator.html", locator.locateSpecification(new FixtureType(this.getClass()), "html").getPath());
    }

    public void testRemovesWordFixtureFromEndOfClassNameAndAppendsDotHTML() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/spec/concordion/common/command/execute/ContinueAfterExceptions.html",
                locator.locateSpecification(new FixtureType(ContinueAfterExceptionsFixture.class), "html").getPath());
    }

    public void testCanAppendDotXHTMLWhenConstructedWithXHTMLArgument() throws Exception {
        SpecificationLocator locator = new ClassNameBasedSpecificationLocator();
        assertEquals("/org/concordion/internal/ClassNameBasedSpecificationLocator.xhtml", locator.locateSpecification(new FixtureType(this.getClass()), "xhtml").getPath());
    }
}
