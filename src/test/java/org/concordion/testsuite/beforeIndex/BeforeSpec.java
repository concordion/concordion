package org.concordion.testsuite.beforeIndex;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class BeforeSpec {
    
    public boolean beforeMethodCalled = false;
    
    @Before
    public void beforeMethod() {
        beforeMethodCalled = true;
    }
    
    public boolean wasBeforeMethodCalled() {
        return beforeMethodCalled;
    }
}
