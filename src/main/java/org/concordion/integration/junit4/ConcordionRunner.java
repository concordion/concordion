package org.concordion.integration.junit4;

import org.concordion.Concordion;
import org.concordion.api.FailFast;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionAssertionError;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.SummarizingResultRecorder;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ConcordionRunner extends BlockJUnit4ClassRunner {

    // this sort of thing is so much easier with Java 8!
    public ConcordionFrameworkMethod.ConcordionRunnerInterface concordionRunnerInterface = new ConcordionFrameworkMethod.ConcordionRunnerInterface() {
        @Override
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

        fixtureRunner = new FixtureRunner(fixture);
        concordion = fixtureRunner.getConcordion();

        try {
            List<String> examples = concordion.getExampleNames(fixture);
            concordionFrameworkMethods = new ArrayList<ConcordionFrameworkMethod>(examples.size());
            for (String example: examples) {
                concordionFrameworkMethods.add(new ConcordionFrameworkMethod(concordionRunnerInterface, example));
            }
        } catch (IOException e) {
            throw new InitializationError(e);
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

            invokeMethodsWithAnnotation(fixtureClass, fixture, Before.class);
            ResultSummary result = fixtureRunner.run(example);

//            System.err.printf("Accumulated %s into %s\n",
//                    result.printToString(fixture),
//                    accumulatedResultSummary.printToString(fixture));

            invokeMethodsWithAnnotation(fixtureClass, fixture, After.class);
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

    private void invokeMethodsWithAnnotation(Class<?> fixtureClass, Object fixture, Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getMethodsWithAnnotation(fixtureClass, annotation);
        for (Method m: methods) {
            m.invoke(fixture, new Object[] {});
        }
    }

    private static List<Method> getMethodsWithAnnotation( Class<?> type, Class<? extends Annotation> annotation) {
        List<Method> foundMethods = new ArrayList<Method>();
        Class<?> currentClass = type;
        while (currentClass != Object.class) {
            // We iterate up through the class heirarchy ensuring we get all the annotated methods.
            Method[] allMethods = currentClass.getDeclaredMethods();
            for ( Method method : allMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    foundMethods.add(method);
                }
            }
            // move up oner class in the hierarchy to search for more methods
            currentClass = currentClass.getSuperclass();
        }
        return foundMethods;
    }

    @Override @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {

    }

}
