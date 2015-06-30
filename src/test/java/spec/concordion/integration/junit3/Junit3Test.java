package spec.concordion.integration.junit3;

import org.concordion.integration.junit3.ConcordionTestCase;

public class Junit3Test extends ConcordionTestCase {

    private boolean setUpCalled = false;

    @Override
    public void setUp() {
        setUpCalled = true;
    }

    public boolean wasSetUpCalled() {
        return setUpCalled;
    }
}
