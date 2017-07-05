package spec.concordion.integration.junit5;

import org.concordion.Concordion;
import org.concordion.api.Fixture;
import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.UnableToBuildConcordionException;
import org.concordion.internal.cache.RunResultsCache;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junit.jupiter.api.extension.ExtensionContext.*;

/**
 * Created by tim on 4/07/17.
 */
public class ConcordionJUnit5Extension implements
        BeforeTestExecutionCallback,
        TestInstancePostProcessor,
        AfterAllCallback
{
    private static final Namespace NAMESPACE =
            Namespace.create("org", "concordion", "ConcordionJUnit5Extension");


    ConcordionJUnit5Extension() {

    }

    @Override
    public void beforeTestExecution(TestExtensionContext context) throws Exception {
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        final Fixture setupFixture;
        try {
            setupFixture = createFixture(testInstance);
            // needs to be called so extensions have access to scoped variables
        } catch (Exception e) {
            throw new InitializationError(e);
        }

        final FixtureRunner fixtureRunner;
        try {
            fixtureRunner = new FixtureRunner(setupFixture);
        } catch (UnableToBuildConcordionException e) {
            throw new InitializationError(e);
        }
        Concordion concordion = fixtureRunner.getConcordion();
        context.getStore(NAMESPACE).put("concordion", concordion);

        concordion.checkValidStatus(setupFixture);

        List<String> examples = concordion.getExampleNames(setupFixture);

        final List<DynamicTest> tests = new LinkedList<>();

        for (final String example : examples) {
            tests.add(dynamicTest(example, () -> {
                RunResultsCache.SINGLETON.startFixtureRun(setupFixture, concordion.getSpecificationDescription());
                ResultSummary result = fixtureRunner.run(example, setupFixture);
                result.assertIsSatisfied(setupFixture);
            }));
        }

        for(Field field  : testInstance.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(ConcordionTests.class))
            {
                field.set(testInstance, tests);
            }
        }
    }


    protected Fixture createFixture(Object fixtureObject) {
        return new FixtureInstance(fixtureObject);
    }

    @Override
    public void afterAll(ContainerExtensionContext context) throws Exception {
        System.err.println("finishing");
        Concordion concordion = (Concordion) context.getStore(NAMESPACE).get("concordion");
        concordion.finish();
    }
}
