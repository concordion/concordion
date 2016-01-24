package spec.concordion.integration.junit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;

import static org.junit.Assert.assertTrue;

public class RulesSupportedByJunit extends Statement {

    @Rule
    public RuleWithCallback rule = new RuleWithCallback(this);
    private boolean ruleInvoked = false;

    @Test
    public void something() {
        assertTrue(ruleInvoked);
    }

    @Override
    public void evaluate() throws Throwable {
        ruleInvoked = true;
    }
}
