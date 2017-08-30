package spec.concordion.integration.junit5;

import org.concordion.Concordion;
import org.concordion.api.Fixture;
import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.UnableToBuildConcordionException;
import org.concordion.internal.cache.RunResultsCache;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.extension.*;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.extension.ExtensionContext.*;

/**
 * Created by tim on 4/07/17.
 */
public class ConcordionJUnit5Extension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        TestInstancePostProcessor,
        AfterAllCallback,
        ParameterResolver
{
    private static final Namespace NAMESPACE =
            Namespace.create("org", "concordion", "ConcordionJUnit5Extension");
    public static final String CONCORDION_NAMESPACE_KEY = "concordion";
    public static final String DYNAMIC_TEST_LIST_NAMESPACE_KEY = "tests";
    private static final Object SETUP_FIXTURE_NAMESPACE_KEY = "setupFixture";

    ConcordionJUnit5Extension() {
    }

    private Fixture getSetupFixture(ExtensionContext context) {
        return (Fixture) context.getStore(NAMESPACE).get(SETUP_FIXTURE_NAMESPACE_KEY);
    }
    private void setSetupFixture(ExtensionContext context, Fixture setupFixture) {
        context.getStore(NAMESPACE).put(SETUP_FIXTURE_NAMESPACE_KEY, setupFixture);
    }
    private Concordion getConcordion(ExtensionContext context) {
        return (Concordion) context.getStore(NAMESPACE).get(CONCORDION_NAMESPACE_KEY);
    }
    private void setConcordion(ExtensionContext context, Concordion concordion) {
        context.getStore(NAMESPACE).put(CONCORDION_NAMESPACE_KEY, concordion);
    }
    private List<DynamicTest> getDynamicTestList(ExtensionContext extensionContext) {
        return (List<DynamicTest>) extensionContext.getStore(NAMESPACE).get(DYNAMIC_TEST_LIST_NAMESPACE_KEY);
    }
    private void setDynamicTestList(ExtensionContext context, List<DynamicTest> tests) {
        context.getStore(NAMESPACE).put(DYNAMIC_TEST_LIST_NAMESPACE_KEY, tests);
    }



    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        final Fixture setupFixture;
        try {
            setupFixture = createFixture(testInstance);
            setSetupFixture(context, setupFixture);

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
        setConcordion(context, concordion);

        concordion.checkValidStatus(setupFixture);

        List<String> examples = concordion.getExampleNames(setupFixture);

        final List<DynamicTest> tests = new LinkedList<>();

        for (final String example : examples) {
            tests.add(DynamicTest.dynamicTest(example, () -> {
                RunResultsCache.SINGLETON.startFixtureRun(setupFixture, concordion.getSpecificationDescription());
                ResultSummary result = fixtureRunner.run(example, setupFixture);
                result.assertIsSatisfied(setupFixture);
            }));
        }

        setDynamicTestList(context, tests);

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
    public void afterAll(ExtensionContext context) throws Exception {
        Concordion concordion = getConcordion(context);
        concordion.finish();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        getSetupFixture(context).afterExample(context.getDisplayName());

    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        getSetupFixture(context).beforeExample(context.getDisplayName());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(Iterable.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getDynamicTestList(extensionContext);
    }
}
