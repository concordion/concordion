package spec.concordion.integration.junit5.testEngine;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcordionTestEngineScannerTest {
    @Test
    void itCanScanAPackage() {
        ConcordionTestEngineScanner scanner = new ConcordionTestEngineScanner();
        Collection<Class<?>> classes = scanner.scan("spec.concordion.integration.junit5.testEngine.testClasses.scanner.single");
        assertEquals(1, classes.size());
    }

    @Test
    void scansChildrenPackages() {
        ConcordionTestEngineScanner scanner = new ConcordionTestEngineScanner();
        Collection<Class<?>> classes = scanner.scan("spec.concordion.integration.junit5.testEngine.testClasses.scanner.withChildren");
        assertEquals(1, classes.size());
    }

    @Test
    void itExcludesClassesWithoutAnAnnotation() {
        ConcordionTestEngineScanner scanner = new ConcordionTestEngineScanner();
        Collection<Class<?>> classes = scanner.scan("spec.concordion.integration.junit5.testEngine.testClasses.scanner.someNoAnnotation");
        assertEquals(0, classes.size());
    }
    @Test
    void itExcludesClassesWithAnnotationAndWithTestMethod() {
        ConcordionTestEngineScanner scanner = new ConcordionTestEngineScanner();
        Collection<Class<?>> classes = scanner.scan("spec.concordion.integration.junit5.testEngine.testClasses.scanner.withTestMethod");
        assertEquals(0, classes.size());
    }
    @Test
    void itExcludesClassesWithAnnotationAndWithTestFactoryMethod() {
        ConcordionTestEngineScanner scanner = new ConcordionTestEngineScanner();
        Collection<Class<?>> classes = scanner.scan("spec.concordion.integration.junit5.testEngine.testClasses.scanner.withTestFactoryMethod");
        assertEquals(0, classes.size());
    }
}