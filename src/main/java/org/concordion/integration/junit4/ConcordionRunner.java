package org.concordion.integration.junit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.concordion.Concordion;
import org.concordion.api.FailFast;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionAssertionError;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.UnableToBuildConcordionException;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.concordion.internal.cache.RunResultsCache;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ConcordionRunner extends BlockJUnit4ClassRunner {

    // this sort of thing is so much easier with Java 8!
    public ConcordionFrameworkMethod.ConcordionRunnerInterface concordionRunnerInterface = new ConcordionFrameworkMethod.ConcordionRunnerInterface() {
        public void invoke(ConcordionFrameworkMethod concordionFrameworkMethod) {
            ConcordionRunner.this.invoke(concordionFrameworkMethod);
        }
    };

    private final Class<?> fixtureClass;
    private final FixtureRunner fixtureRunner;
    private final Concordion concordion;
    private final List<ConcordionFrameworkMethod> concordionFrameworkMethods;
    private final Object fixture;
    private SummarizingResultRecorder accumulatedResultSummary;


    private FailFastException failFastException = null;

    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
        this.fixtureClass = fixtureClass;
        this.accumulatedResultSummary = new SummarizingResultRecorder();
        accumulatedResultSummary.setSpecificationDescription(Concordion.getDefaultFixtureClassName(fixtureClass));

        try {
            fixture = fixtureClass.newInstance();
        } catch (InstantiationException e) {
            throw new InitializationError(e);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }

        try {
            fixtureRunner = new FixtureRunner(fixture);
        } catch (UnableToBuildConcordionException e) {
            throw new InitializationError(e);
        }
        concordion = fixtureRunner.getConcordion();

        try {
            List<String> examples = concordion.getExampleNames();

            verifyUniqueExampleMethods(examples);

            concordionFrameworkMethods = new ArrayList<ConcordionFrameworkMethod>(examples.size());
            for (String example: examples) {
                concordionFrameworkMethods.add(new ConcordionFrameworkMethod(concordionRunnerInterface, example));
            }
        } catch (IOException e) {
            throw new InitializationError(e);
        }


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
                throw new InitializationError("Specification has duplicate example " + example);
            }
            setOfExamples.add(example);
        }
    }

    // This is important or else jUnit will create lots of different instances of the class under test.
    @Override
    protected Object createTest() throws Exception {
        return fixture;
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);

        concordion.finish();

        ConcordionRunOutput results = RunResultsCache.SINGLETON.getFromCache(fixtureClass, null);
        if (results != null) {
            // we only print meta-results when the spec has multiple examples.
            if (concordionFrameworkMethods.size() > 1) {
                synchronized (System.out) {
                    results.getActualResultSummary().print(System.out, fixture);
                }
            }
        }

        if (failFastException != null) {
            if (fixtureClass.getAnnotation(FailFast.class) != null) {
                throw new FailFastException("Failing Fast", failFastException);
            }
        }
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

    void invoke(ConcordionFrameworkMethod concordionFrameworkMethod) {

        String example = concordionFrameworkMethod.getExampleName();

        // check the fail fast condition
        if (failFastException != null) {
            throw failFastException;
        }

        try {

            // The ParentRunner class invokes the @BeforeClass and @AfterClass methods so we don't need
            // to worry about them. But it doesn't invoke the @Before and @After methods. So we explicitly
            // invoke them here.

            ResultSummary result = fixtureRunner.run(example);

//            System.err.printf("Accumulated %s into %s\n",
//                    result.printToString(fixture),
//                    accumulatedResultSummary.printToString(fixture));


            result.assertIsSatisfied(fixture);


        } catch (ConcordionAssertionError e) {
            accumulatedResultSummary.record(e.getResultSummary());
            throw e;
        } catch (FailFastException e){
            accumulatedResultSummary.record(Result.EXCEPTION);
            failFastException = e;
            throw e;

        }catch (Throwable e) {
            // if *anything* goes wrong, we fire a test failure notification.
            e.printStackTrace(System.err);

        }
    }

    @Override @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {

    }
}
