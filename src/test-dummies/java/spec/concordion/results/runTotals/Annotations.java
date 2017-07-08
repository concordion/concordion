package spec.concordion.results.runTotals;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

@RunWith(ConcordionRunner.class)
public class Annotations {

    private static boolean beforeClassCalled=false;
    private boolean beforeCalled=false;
    private boolean ruleMethodInvoked = false;
    private boolean ruleFieldInvoked = false;

    @Before
    public void before() {
        beforeCalled = true;
    }
    @BeforeClass
    public static void beforeClass() {
        beforeClassCalled = true;
    }

    @Rule
    public TestRule rule = new RuleWithCallback(new Statement() {
        @Override
        public void evaluate() throws Throwable {
            Annotations.this.ruleFieldInvoked = true;
        }
    });

    @Rule
    public TestRule ruleMethod() {
        return new RuleWithCallback(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Annotations.this.ruleMethodInvoked = true;
            }
        });
    }


    public boolean wasBeforeCalled() {
        return beforeCalled;
    }
    public boolean wasBeforeClassCalled() {
        return beforeClassCalled;
    }
    public boolean wasRuleMethodInvoked() {
        return ruleMethodInvoked;
    }
    public boolean wasRuleFieldInvoked() {
        return ruleFieldInvoked;
    }
}
