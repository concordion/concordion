package org.concordion.internal.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.api.Runner;
import org.concordion.api.Unimplemented;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.integration.junit4.ConcordionEnhancedReporting;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.*;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.Statement;

public class DefaultConcordionRunner implements Runner {

    private static Logger logger = Logger.getLogger(DefaultConcordionRunner.class.getName());


    @Override
	public ResultSummary execute(Resource resource, String href) throws Exception {
        Class<?> concordionClass = findTestClass(resource, href);
        return runTestClass(concordionClass);
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
        String name = resource.getName();
        Resource hrefResource = resource.getParent().getRelativeResource(href);
        name = hrefResource.getPath().replaceFirst("/", "").replace("/", ".").replaceAll("\\.html$", "");
        Class<?> concordionClass;
        try {
            concordionClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                concordionClass = Class.forName(name + "Test");
            } catch (ClassNotFoundException e1) {
                concordionClass = Class.forName(name + "Fixture"); // FIXED: Issue 47
            }
        }
        return concordionClass;
    }

    protected ResultSummary runTestClass(Class<?> concordionClass) throws Exception {
        CachedRunResults cache = CachedRunResults.SINGLETON;

        // first check the cache.
        ResultSummary summary = cache.getFromCache(concordionClass);

        // if we found something in the cache, we can do much less work.
        if (summary == null) {

            // Now check the Unimplemented annotation and abort if we find it.
            if (concordionClass.isAnnotationPresent(Unimplemented.class)) {

                summary = new SingleResultSummary(Result.IGNORED);
                // may as well stick it in the cache for next time.
                cache.enterIntoCache(concordionClass, summary);

                logger.info("Returning unimplemented result summay "
                        + summary.printToString(concordionClass.newInstance()));


            } else {

                // Not in cache, test is not @Unimplemented, so run the test...
                org.junit.runner.Result jUnitResult = runJUnitClass(concordionClass);

                // check the cache again - if the test was a concordion test, it will have stuck the results
                // in the cache
                summary = cache.getFromCache(concordionClass);

                // check the test actually put something in the cache
                if (summary == null) {

                    // Nothing in the cache, so create a summary based on the jUnit result
                    summary = decodeJUnitResult(concordionClass, jUnitResult);

                    // and stick it in the cache for next time.
                    cache.enterIntoCache(concordionClass, summary);

                    logger.info("Returning converted jUnit result summary "
                            + summary.printToString(concordionClass.newInstance()));

                } else {

                    logger.info("Returning result summary from executing test "
                            + summary.printToString(concordionClass.newInstance()));
                }

            }
        } else {
            logger.info("Returning cached result summary "
                    + summary.printToString(concordionClass.newInstance()));
        }

        // done! Return the summary
        return summary;
    }



    private void processTestException(Throwable exception,
			Class<?> concordionClass) throws AssertionError, Exception {
        logExceptionIfNotAssertionError(exception);
        rethrowExceptionIfWarranted(concordionClass, exception);
	}

	protected org.junit.runner.Result runJUnitClass(Class<?> concordionClass) {
        org.junit.runner.Result jUnitResult = JUnitCore.runClasses(concordionClass);
        return jUnitResult;
    }

    protected ResultSummary decodeJUnitResult(Class<?> concordionClass, org.junit.runner.Result jUnitResult) throws Exception {
        Result result = Result.FAILURE;
        if (jUnitResult.wasSuccessful()) {
            result = Result.SUCCESS;
            if (onlyPartiallyImplemented(concordionClass)) {
                result = Result.IGNORED;
            }
            if (jUnitResult.getIgnoreCount() > 0) {
                result = Result.IGNORED;
            }
        } else {
            List<Failure> failures = jUnitResult.getFailures();
            for (Failure failure : failures) {
    			processTestException(failure.getException(), concordionClass);
            }
        }

        SummarizingResultRecorder recorder = new SummarizingResultRecorder();
        recorder.record(result);

        return recorder;
    }

    private void logExceptionIfNotAssertionError(Throwable exception) {
        if (!(exception instanceof AssertionError)) {
            logger.log(Level.WARNING, "", exception);
        }
    }

    private void rethrowExceptionIfWarranted(Class<?> concordionClass, Throwable exception) throws AssertionError, Exception {
        if (exception instanceof AssertionError) {
            if (exception.getCause() instanceof FailFastException) {
                throw (FailFastException)(exception.getCause());
            }
            if (fullyImplemented(concordionClass)) {
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

    private boolean onlyPartiallyImplemented(Class<?> concordionClass) {
        return concordionClass.getAnnotation(ExpectedToFail.class) != null
            || concordionClass.getAnnotation(Unimplemented.class) != null;
    }

    private boolean fullyImplemented(Class<?> concordionClass) {
        return !(onlyPartiallyImplemented(concordionClass));
    }

}
