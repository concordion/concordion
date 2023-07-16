package spec.concordion.integration.junit4;

import junit.framework.AssertionFailedError;
import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#endif
public class Junit4Test extends Statement {

    private static boolean beforeClassCalled=false;
    private boolean beforeCalled=false;
    private boolean ruleInvoked = false;

    @Before
    public void before() {
        beforeCalled = true;
    }
    @BeforeClass
    public static void beforeClass() {
        beforeClassCalled = true;
    }

    @Rule
    public TestRule rule = new RuleWithCallback(this);

    @Override
    public void evaluate() throws Throwable {
        ruleInvoked = true;
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

    public String getFooFixtureClass() {
        return FooFixtureRecorder.getFooFixtureClass();
    }
}
