package spec.concordion.integration.junit5;

import org.concordion.api.Fixture;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.UnableToBuildConcordionException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Created by tim on 4/07/17.
 */
//@Concordion
public class Junit5Test {
    private static boolean beforeClassCalled=false;
    private boolean beforeCalled=false;
    private boolean ruleInvoked = false;

    public void before() {
        beforeCalled = true;
    }
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
    Iterable<DynamicTest> determineConcordionTests() throws InitializationError, IOException {

        Fixture setupFixture;
        try {
            setupFixture = createFixture(getFixtureObject());
            // needs to be called so extensions have access to scoped variables
        } catch (Exception e) {
            throw new InitializationError(e);
        }

        FixtureRunner fixtureRunner;
        try {
            fixtureRunner = new FixtureRunner(setupFixture);
        } catch (UnableToBuildConcordionException e) {
            throw new InitializationError(e);
        }
        org.concordion.Concordion concordion = fixtureRunner.getConcordion();

        concordion.checkValidStatus(setupFixture);

        List<String> examples = concordion.getExampleNames(setupFixture);

        final List<DynamicTest> tests = new LinkedList<DynamicTest>();

        for (String example : examples) {
            tests.add(dynamicTest(example, new Executable() {
                public void execute() throws Throwable {
                }
            }));
        }

        return tests;
    }

    protected Fixture createFixture(Object fixtureObject) {
        return new FixtureInstance(fixtureObject);
    }

    private Object getFixtureObject() {
        return this;
    }
}

