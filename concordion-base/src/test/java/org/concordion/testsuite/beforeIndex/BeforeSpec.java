package org.concordion.testsuite.beforeIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class BeforeSpec {

    public static boolean beforeClassMethodCalled = false;
    public boolean beforeMethodCalled = false;

    @BeforeClass
    public static void beforeClass() {
        beforeClassMethodCalled =true;
    }

    @Before
    public void beforeMethod() {
        beforeMethodCalled = true;
    }
    
    public boolean wasBeforeMethodCalled() {
        return beforeMethodCalled;
    }

    public static boolean wasBeforeClassMethodCalled() {
        return beforeClassMethodCalled;
    }
}
