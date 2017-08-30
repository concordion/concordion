package spec.concordion.integration.junit5.jupiterRunner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spec.concordion.integration.junit5.ConcordionJUnit5;
import spec.concordion.integration.junit5.ConcordionTests;

/**
 * Created by tim on 4/07/17.
 */
@ConcordionJUnit5
public class Junit5FieldInjectionTest {
    private static boolean beforeClassCalled = false;
    private boolean beforeCalled = false;

    @BeforeEach
    public void before() {
        beforeCalled = true;
    }

    @BeforeAll
    public static void beforeClass() {
        beforeClassCalled = true;
    }

    public boolean wasBeforeCalled() {
        return beforeCalled;
    }
    public boolean wasBeforeClassCalled() {
        return beforeClassCalled;
    }

    @ConcordionTests
    public Iterable<DynamicTest> concordionTests;

    @TestFactory
    Iterable<DynamicTest> determineConcordionTests() {
        return concordionTests;
    }
}

