package spec.concordion.integration.junit5;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Created by tim on 4/07/17.
 */
@ConcordionJUnit5
public class Junit5ParamResolverTest {
    private static boolean beforeClassCalled=false;
    private boolean beforeCalled=false;
    private boolean ruleInvoked = false;

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
    public boolean wasRuleInvoked() {
        return ruleInvoked;
    }

    @TestFactory
    Iterable<DynamicTest> determineConcordionTests(Iterable<DynamicTest> concordionTests) {
        return concordionTests;
    }
}

