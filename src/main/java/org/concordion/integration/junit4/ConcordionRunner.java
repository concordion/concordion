package org.concordion.integration.junit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.Concordion;
import org.concordion.api.Fixture;
import org.concordion.api.ResultSummary;
import org.concordion.api.SpecificationLocator;
import org.concordion.internal.*;
import org.concordion.internal.cache.RunResultsCache;
import org.concordion.internal.extension.FixtureExtensionLoader;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ConcordionRunner extends BlockJUnit4ClassRunner {

    private final RunResultsCache runResultsCache = RunResultsCache.SINGLETON;

    // this sort of thing is so much easier with Java 8!
    public ConcordionFrameworkMethod.ConcordionRunnerInterface concordionRunnerInterface =
            new ConcordionFrameworkMethod.ConcordionRunnerInterface() {
        public void invoke(ConcordionFrameworkMethod concordionFrameworkMethod, Object target) throws Exception {
            ConcordionRunner.this.invoke(concordionFrameworkMethod, target);
        }
    };

    private final Class<?> fixtureClass;
    private final List<ConcordionFrameworkMethod> concordionFrameworkMethods;

    private Fixture setupFixture;
    private FixtureRunner fixtureRunner;
    private Concordion concordion;

    private FailFastException failFastException = null;

    private static AtomicInteger suiteDepth = new AtomicInteger();
    private boolean firstTest = true;
    private boolean setUpDone = false;

    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
        this.fixtureClass = fixtureClass;

        try {
            FixtureType fixtureType = new FixtureType(fixtureClass);
            // Avoiding the instantiation of the fixture object at this point
            ConcordionBuilder localBuilder = new ConcordionBuilder()
                    .withFixtureType(fixtureType)
                    .withSpecificationLocator(getSpecificationLocator());
            new FixtureExtensionLoader().addExtensions(fixtureType, localBuilder);
            new FixtureOptionsLoader().addOptions(fixtureType, localBuilder);
            Concordion localConcordion = localBuilder.build(false);
            localConcordion.checkValidStatus(fixtureType);

            List<String> examples = localConcordion.getExampleNames(fixtureType);

            verifyUniqueExampleMethods(examples);

            concordionFrameworkMethods = new ArrayList<ConcordionFrameworkMethod>(examples.size());
            for (String example: examples) {
                concordionFrameworkMethods.add(new ConcordionFrameworkMethod(concordionRunnerInterface, example));
            }
        } catch (UnableToBuildConcordionException e) {
            throw new InitializationError(e);
        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    protected SpecificationLocator getSpecificationLocator() {
        return new ClassNameBasedSpecificationLocator();
    }

    private void verifyUniqueExampleMethods(List<String> exampleNames) throws InitializationError {
        // use a hash set to store examples - gives us quick lookup and add.
        Set<String> setOfExamples = new HashSet<String>();

        for (String example: exampleNames) {
            int questionPlace = example.indexOf('?');

            if (questionPlace >=0 ) {
                example = example.substring(0, questionPlace);
            }

            if (setOfExamples.contains(example)) {
                throw new InitializationError("Specification has duplicate example: '" + example + "'");
            }
            setOfExamples.add(example);
        }
    }

    @Override
    protected Object createTest() throws Exception {
        Object fixtureObject;
        if (firstTest) {
            firstTest = false;
            // we've already created a test object above, so reuse it to make sure we don't initialise the fixture object multiple times
            fixtureObject = setupFixture.getFixtureObject();
        } else {
            // junit creates a new object for each test case, so we need to capture this
            // and setup our object - that makes sure that scoped variables are injected properly
            fixtureObject = super.createTest();
        }

        // we need to setup the concordion scoped objects so that the @Before methods and @Rules can access them
        setupFixture.setupForRun(fixtureObject);

        return fixtureObject;
    }

    /**
     *
     * Protected so superclasses can change the Fixture being returned.
     *
     * @param fixtureObject fixture instance
     * @return fixture
     */
    protected Fixture createFixture(Object fixtureObject) {
        return new FixtureInstance(fixtureObject);
    }

    @Override
    public void run(RunNotifier notifier) {

        setUp();

        try {
            // we figure out if the spec has been run before by checking if there are any
            // prior results in the cache
            boolean firstRun = null == runResultsCache.getFromCache(fixtureClass, null);

            // only setup the fixture if it hasn't been run before
            if (firstRun) {
                runResultsCache.startFixtureRun(setupFixture.getFixtureType(), concordion.getSpecificationDescription());
                setupFixture.beforeSpecification();
            }

            super.run(notifier);

            // only actually finish the specification if this is the first time it was run
            if (firstRun) {
                setupFixture.afterSpecification();
                concordion.finish();
            }

            RunOutput results = runResultsCache.getFromCache(fixtureClass, null);

            if (results != null) {
                synchronized (System.out) {
                    results.getActualResultSummary().print(System.out, setupFixture.getFixtureType());
                }
            }
        } catch (RuntimeException e) {
            notifier.fireTestFailure(new Failure(getDescription(), e));
            throw e;
        } finally {
            if (suiteDepth.decrementAndGet() == 0) {
                setupFixture.afterSuite();
            }
        }
    }

    private void setUp() {
        if (setUpDone) return;

        try {
            setupFixture = createFixture(super.createTest());
            // needs to be called so extensions have access to scoped variables
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (suiteDepth.getAndIncrement() == 0) {
            setupFixture.beforeSuite();
        }

        try {
            fixtureRunner = new FixtureRunner(setupFixture, getSpecificationLocator());
        } catch (UnableToBuildConcordionException e) {
            throw new RuntimeException(e);
        }
        concordion = fixtureRunner.getConcordion();
        try {
            concordion.getExampleNames(setupFixture.getFixtureType()); // (force) load specification from resource
            // needs to be called so extensions are initialized
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setUpDone = true;
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        // downcast from ConcordionFrameworkMethod to FrameworkMethod
        ArrayList<FrameworkMethod> frameworkMethods = new ArrayList<FrameworkMethod>(concordionFrameworkMethods);
        return frameworkMethods;
    }

    @Override
    protected Description describeChild(FrameworkMethod frameworkMethod) {
        ConcordionFrameworkMethod concordionFrameworkMethod = (ConcordionFrameworkMethod) frameworkMethod;
        return Description.createTestDescription(fixtureClass, concordionFrameworkMethod.getExampleName());
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        ((ConcordionFrameworkMethod) method).setNotifier(notifier);
        super.runChild(method, notifier);
    }

    void invoke(ConcordionFrameworkMethod concordionFrameworkMethod, Object target) throws Exception {

        // create the new fixture because there is a new fixture object.
        Fixture fixture = createFixture(target);

        String example = concordionFrameworkMethod.getExampleName();

        // check the fail fast condition
        if (failFastException != null) {
            throw failFastException;
        }

        try {
            ResultSummary result = fixtureRunner.run(example, fixture);
            result.assertIsSatisfied(fixture.getFixtureType());

        } catch (ConcordionAssertionError e) {
            throw e;
        } catch (FailFastException e){
            failFastException = e;
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    @Override @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {

    }
}
