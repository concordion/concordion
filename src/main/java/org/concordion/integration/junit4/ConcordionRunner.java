package org.concordion.integration.junit4;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.concordion.Concordion;
import org.concordion.api.*;
import org.concordion.internal.ConcordionAssertionError;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.UnableToBuildConcordionException;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.concordion.internal.cache.RunResultsCache;
import org.concordion.internal.scopedObjects.ConcordionScopedObjectFactory;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ConcordionRunner extends BlockJUnit4ClassRunner {

    // this sort of thing is so much easier with Java 8!
    public ConcordionFrameworkMethod.ConcordionRunnerInterface concordionRunnerInterface =
            new ConcordionFrameworkMethod.ConcordionRunnerInterface() {
        public void invoke(ConcordionFrameworkMethod concordionFrameworkMethod, Object target) throws Exception {
            ConcordionRunner.this.invoke(concordionFrameworkMethod, target);
        }
    };

    private final Class<?> fixtureClass;
    private final FixtureRunner fixtureRunner;
    private final Concordion concordion;
    private final List<ConcordionFrameworkMethod> concordionFrameworkMethods;
    private SummarizingResultRecorder accumulatedResultSummary;


    private FailFastException failFastException = null;
    private Fixture setupFixture;

    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
        this.fixtureClass = fixtureClass;
        this.accumulatedResultSummary = new SummarizingResultRecorder();

        try {
            setupFixture = new Fixture(fixtureClass.newInstance());
            // needs to be called so extensions have access to scoped variables
            ConcordionScopedObjectFactory.SINGLETON.setupFixture(setupFixture);
        } catch (InstantiationException e) {
            throw new InitializationError(e);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
        accumulatedResultSummary.setSpecificationDescription(setupFixture.getDescription());

        try {
            fixtureRunner = new FixtureRunner(setupFixture);
        } catch (UnableToBuildConcordionException e) {
            throw new InitializationError(e);
        }
        concordion = fixtureRunner.getConcordion();

        try {
            List<String> examples = concordion.getExampleNames(setupFixture);

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

    @Override
    protected Object createTest() throws Exception {
        Fixture fixture = new Fixture(super.createTest());
        // we need to setup the concordion scoped objects so that the @Before methods and @Rules can access
        // them
        ConcordionScopedObjectFactory.SINGLETON.setupFixture(fixture);
        return fixture.getFixtureObject();
    }

    @Override
    public void run(RunNotifier notifier) {

        ConcordionRunOutput results = RunResultsCache.SINGLETON.getFromCache(fixtureClass, null);

        invokeMethods(setupFixture, BeforeSpecification.class);

        super.run(notifier);

        invokeMethods(setupFixture, AfterSpecification.class);

        // only actually finish the specification if it has not already been run.
        if (results == null) {
            concordion.finish();
        }

        results = RunResultsCache.SINGLETON.getFromCache(fixtureClass, null);

        if (results != null) {
            // we only print meta-results when the spec has multiple examples.
            if (concordionFrameworkMethods.size() > 1) {
                synchronized (System.out) {
                    results.getActualResultSummary().print(System.out, setupFixture);
                }
            }
        }

        if (failFastException != null) {
            if (fixtureClass.getAnnotation(FailFast.class) != null) {
                throw new FailFastException("Failing Fast", failFastException);
            }
        }
    }

    private void invokeMethods(Fixture fixture, Class<? extends Annotation> annotation) {

        Method[] methods = fixture.getFixtureClass().getMethods();

        for (Method method: methods) {
//            Annotation a = method.getAnnotation(annotation);
            if (method.isAnnotationPresent(annotation)) {
                try {
                    method.setAccessible(true);
                    method.invoke(fixture.getFixtureObject(), new Object[] {});
                } catch (IllegalAccessException e) {
                    throw new AnnotationFormatError("Invalid permissions to invoke method: " + method.getName());
                } catch (InvocationTargetException e) {
                    throw new AnnotationFormatError("Could not invoke method with no arguments: " + method.getName());
                }
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

    void invoke(ConcordionFrameworkMethod concordionFrameworkMethod, Object target) throws Exception {

        Fixture fixture = new Fixture(target);

        String example = concordionFrameworkMethod.getExampleName();

        // check the fail fast condition
        if (failFastException != null) {
            throw failFastException;
        }

        try {

            ResultSummary result = fixtureRunner.run(example, fixture);

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
        } catch (IOException e) {
            accumulatedResultSummary.record(Result.EXCEPTION);
            throw e;
        }
//        catch (Throwable e) {
//            // if *anything* goes wrong, we fire a test failure notification.
//            e.printStackTrace(System.err);
//
//        }
    }

    @Override @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {

    }
}
