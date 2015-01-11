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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ConcordionRunner extends ParentRunner<String> {


    private final Class<?> fixtureClass;
    private final FixtureRunner fixtureRunner;
    private ResultSummary result;
    private final Concordion concordion;
    private final List<String> exampleNames;
    private final Object fixture;
    private SummarizingResultRecorder accumulatedResultSummary;

    private FailFastException failFastException = null;


    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
        this.fixtureClass = fixtureClass;
        this.accumulatedResultSummary = new SummarizingResultRecorder();
        accumulatedResultSummary.setSpecificationDescription(Concordion.getDefaultFixtureClassName(fixtureClass));

        // check all instance methods are setup correctly
        List<Throwable> errors = new ArrayList<Throwable>();
        validateFixtureClass(errors);
        if (errors.size() > 0) {
            throw new InitializationError(errors);
        }

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
            exampleNames = concordion.getExampleNames(fixture);
        } catch (IOException e) {
            throw new InitializationError(e);
        }
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
    protected List<String> getChildren() {
        return exampleNames;
    }

    @Override
    protected Description describeChild(String example) {
        return Description.createTestDescription(fixtureClass, example);
    }

    @Override
    protected void runChild(String example, RunNotifier notifier) {

        Description description = describeChild(example);

        // check the fail fast condition
        if (failFastException != null) {
            notifier.fireTestStarted(description);
            notifier.fireTestFailure(new Failure(description, failFastException));
            notifier.fireTestFinished(description);
            return;
        }


        try {

            // The ParentRunner class invokes the @BeforeClass and @AfterClass methods so we don't need
            // to worry about them. But it doesn't invoke the @Before and @After methods. So we explicitly
            // invoke them here.

            invokeMethodsWithAnnotation(fixtureClass, fixture, Before.class);
            notifier.fireTestStarted(description);
            ResultSummary result = fixtureRunner.run(example);

//            System.err.printf("Accumulated %s into %s\n",
//                    result.printToString(fixture),
//                    accumulatedResultSummary.printToString(fixture));

            invokeMethodsWithAnnotation(fixtureClass, fixture, After.class);
//            result.assertIsSatisfied(fixture);

            accumulatedResultSummary.record(result);

        } catch (ConcordionAssertionError e) {
            notifier.fireTestFailure(new Failure(description, e));
            accumulatedResultSummary.record(e.getResultSummary());
        } catch (FailFastException e){
            notifier.fireTestFailure(new Failure(description, e));
            accumulatedResultSummary.record(Result.EXCEPTION);
            failFastException = e;
            throw e;

        }catch (Throwable e) {
            // if *anything* goes wrong, we fire a test failure notification.

            notifier.fireTestFailure(new Failure(description, e));
            accumulatedResultSummary.record(Result.EXCEPTION);
            e.printStackTrace(System.err);

        }  finally {
            // we fire the finish even in failure cases - so having it in the finally block makes sense.
            notifier.fireTestFinished(description);
        }
    }

    private void invokeMethodsWithAnnotation(Class<?> fixtureClass, Object fixture, Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getMethodsWithAnnotation(fixtureClass, annotation);
        for (Method m: methods) {
            m.invoke(fixture, new Object[] {});
        }
    }

    private void invokeStaticMethodsWithAnnotation(Class<?> fixtureClass, Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getMethodsWithAnnotation(fixtureClass, annotation);
        for (Method m: methods) {
            m.invoke(null, new Object[] {});
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

    protected void validateFixtureClass(List<Throwable> errors) {

        validatePresenceOfAConstructorThatTakesNoArguments(errors);

    	validatePublicVoidNoArgMethods(BeforeClass.class, true, errors);
    	validatePublicVoidNoArgMethods(AfterClass.class, true, errors);
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
    }

    private void validatePresenceOfAConstructorThatTakesNoArguments(List<Throwable> errors) {
        try {
            fixtureClass.getConstructor(new Class[] {});
        } catch (NoSuchMethodException e) {
            errors.add(e);
        }
    }

    public ResultSummary getAccumulatedResultSummary() {
        return accumulatedResultSummary;
    }

}
