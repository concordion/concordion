package org.concordion.internal.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.concordion.api.*;
import org.concordion.integration.junit.platform.engine.ConcordionTestEngine;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureSpecificationMapper;
import org.concordion.internal.FixtureType;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.RunOutput;
import org.concordion.internal.cache.RunResultsCache;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * A {@link org.concordion.api.Runner run-command runner} that uses JUnit
 * Platform engines (e.g. "concordion", "junit-vintage") to run the fixture
 * class' specification/examples. The fixture class is usually annotated with
 * <code>{@link org.concordion.api.ConcordionFixture &#64;ConcordionFixture}</code>
 * or <code>{@literal @}RunWith(ConcordionRunner.class)</code>.
 *
 * <p>
 * Not to be confused with
 * {@link org.concordion.integration.junit4.ConcordionRunner ConcordionRunner}
 * which is a {@link org.junit.runner.Runner Runner} used with
 * {@link org.junit.runner.RunWith &#64;RunWith} annotation.
 * </p>
 */
public class DefaultConcordionRunner implements Runner {

    public ResultSummary execute(Resource resource, String href) throws Exception {
        Class<?> fixtureClass = findTestClass(resource, href);
        return runTestClass(fixtureClass);
    }

    /**
     * Finds the test class for the specification referenced by the given href, relative to the
     * resource.
     *
     * @param resource the current resource
     * @param href the specification to find the test class for
     * @return test class
     * @throws ClassNotFoundException if test class not found
     */
    protected Class<?> findTestClass(Resource resource, String href) throws ClassNotFoundException {
        Resource hrefResource = resource.getParent().getRelativeResource(href);
        return FixtureSpecificationMapper.findFixtureClass(hrefResource);
    }

    protected ResultSummary runTestClass(Class<?> fixtureClass) throws Exception {
        RunResultsCache cache = RunResultsCache.SINGLETON;

        // first check the cache.
        ResultSummary summary = null;

        // we always run the test to ensure that the FixtureRunner class
        // has an opportunity to print out any necessary debugging information.
        TestExecutionSummary jUnitResult = runJUnitPlatformEngine(fixtureClass, resolveEngineId(fixtureClass));

        // we always decode the jUnit summary because it handles specification exceptions
        // (as opposed to exceptions that occurred in a specification) better.
        ResultSummary jUnitSummary = decodeJUnitPlatformEngineResult(jUnitResult, fixtureClass);

        // check the cache - if the test was a concordion test, it will have stuck the results
        // in the cache. Use "null" for the example to get the accumulated values from all examples
        RunOutput concordionRunOutput = cache.getFromCache(fixtureClass, null);

        // check the test actually put something in the cache
        if (concordionRunOutput == null) {
            summary = jUnitSummary;
        } else {
            summary = concordionRunOutput.getModifiedResultSummary();
        }

        // throw an exception if we're failing fast...
        if (summary instanceof SummarizingResultRecorder) {
            if (((SummarizingResultRecorder) summary).getFailFastException() != null) {
                throw ((SummarizingResultRecorder) summary).getFailFastException();
            }
        }

        return summary;
    }

    String resolveEngineId(Class<?> fixtureClass) {
        // Use JUnit Platform to:
        // - Use "junit-vintage" engine when fixture class has @RunWith(ConcordionRunner.class)
        // - Use "concordion" engine otherwise (usually fixture class has @ConcordionFixture)
        return hasRunWithConcordionRunner(fixtureClass)
                ? "junit-vintage" // See: org.junit.vintage.engine.descriptor.VintageTestDescriptor.ENGINE_ID
                : ConcordionTestEngine.ENGINE_ID;
    }

    @SuppressWarnings("unchecked")
    boolean hasRunWithConcordionRunner(Class<?> fixtureClass) {
        // All this to avoid compile/build-time dependencies on JUnit 4.x
        if (isJUnit4RunWithAnnotationPresent()) {
            Class<? extends Annotation> runWithAnnotationClass;
            try {
                runWithAnnotationClass =
                        (Class<? extends Annotation>) Class.forName(JUNIT4_RUNWITH_ANNOTATION);
                Optional<? extends Annotation> runWithAnnotation =
                        AnnotationSupport.findAnnotation(fixtureClass, runWithAnnotationClass);
                return runWithAnnotation.isPresent()
                        && ConcordionRunner.class.isAssignableFrom(
                            (Class<?>) getAttribute(runWithAnnotation.get(), "value"));
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    private static final String JUNIT4_RUNWITH_ANNOTATION = "org.junit.runner.RunWith";

    private boolean isJUnit4RunWithAnnotationPresent() {
        return isPresent(JUNIT4_RUNWITH_ANNOTATION);
    }

    private boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private Object getAttribute(Annotation annotation, String attributeName) {
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName);
            return ReflectionSupport.invokeMethod(method, annotation);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    protected TestExecutionSummary runJUnitPlatformEngine(Class<?> fixtureClass, String engineId) {
        // See: https://junit.org/junit5/docs/current/user-guide/#launcher-api-execution
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .filters(EngineFilter.includeEngines(engineId))
                .selectors(DiscoverySelectors.selectClass(fixtureClass))
                .build();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        try (LauncherSession session = LauncherFactory.openSession()) {
            Launcher launcher = session.getLauncher();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);
        }
        return listener.getSummary();
    }

    protected ResultSummary decodeJUnitPlatformEngineResult(TestExecutionSummary summary, Class<?> fixtureClass)
            throws AssertionError, Exception {
        Result result = Result.FAILURE;
        FixtureType fixtureDeclaration = new FixtureType(fixtureClass);
        if (summary.getTestsFailedCount() == 0) {
            result = Result.SUCCESS;
            if (onlyPartiallyImplemented(fixtureDeclaration)) {
                result = Result.IGNORED;
            }
            if (summary.getTestsSkippedCount() > 0) {
                result = Result.IGNORED;
            }
        }
        else {
            List<TestExecutionSummary.Failure> failures = summary.getFailures();
            for (TestExecutionSummary.Failure failure : failures) {
                processTestException(fixtureDeclaration, failure.getException());
            }
        }

        SummarizingResultRecorder recorder = new SummarizingResultRecorder();
        recorder.record(result);

        return recorder;
    }

    private void processTestException(FixtureType fixtureDeclaration,
            Throwable exception) throws AssertionError, Exception {
        logExceptionIfNotAssertionError(exception);
        rethrowExceptionIfWarranted(fixtureDeclaration, exception);
    }

    private void logExceptionIfNotAssertionError(Throwable exception) {
        if (!(exception instanceof AssertionError)) {
            System.err.println(exception.getClass().getName() + ": " + exception.getLocalizedMessage());
        }
    }

    private void rethrowExceptionIfWarranted(FixtureType fixtureDeclaration, Throwable exception) throws AssertionError, Exception {
        if (exception instanceof AssertionError) {
            if (exception.getCause() instanceof FailFastException) {
                throw (FailFastException)(exception.getCause());
            }
            if (fullyImplemented(fixtureDeclaration)) {
                return; // do not throw exception, failing specs are treated as test failures.
            } else {
                // However if the spec is partially implemented we do want an exception so that the caller can be informed of the reason.
                throw (AssertionError) exception;
            }
        }
        if (exception instanceof Exception) {
            throw (Exception) exception;
        }
        throw new RuntimeException(exception);
    }

    private boolean onlyPartiallyImplemented(FixtureType fixtureDeclaration) {
        return fixtureDeclaration.declaresStatus(ImplementationStatus.EXPECTED_TO_FAIL) ||
               fixtureDeclaration.declaresStatus(ImplementationStatus.UNIMPLEMENTED);
    }

    private boolean fullyImplemented(FixtureType fixtureDeclaration) {
        return !(onlyPartiallyImplemented(fixtureDeclaration));
    }

}
