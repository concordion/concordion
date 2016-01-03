package org.concordion.internal.runner;

import java.util.List;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.api.Runner;
import org.concordion.api.Unimplemented;
import org.concordion.internal.CachedRunResults;
import org.concordion.internal.ConcordionRunOutput;
import org.concordion.internal.FailFastException;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.util.Check;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

public class DefaultConcordionRunner implements Runner {

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
        ResultSummary summary = null;

        // we always run the test to ensure that the FixtureRunner class
        // has an opportuinity to print out any necessary debugging information.
        org.junit.runner.Result jUnitResult = runJUnitClass(concordionClass);

        // we always decode the jUnut summary because it handles specification exceptions
        // (as opposed to exceptions that occured in a specification) better.
        ResultSummary jUnitSummary = decodeJUnitResult(concordionClass, jUnitResult);

        // check the cache - if the test was a concordion test, it will have stuck the results
        // in the cache
        ConcordionRunOutput concordionRunOutput = cache.getFromCache(concordionClass);

        // check the test actually put something in the cache
        if (concordionRunOutput == null) {
            summary = jUnitSummary;
        } else {
            summary = concordionRunOutput.getPostProcessedResultSummary();
        }

        // throw an exception if we're failing fast...
        if (summary instanceof SummarizingResultRecorder) {
            if (((SummarizingResultRecorder) summary).getFailFastException() != null) {
                throw ((SummarizingResultRecorder) summary).getFailFastException();
            }
        }

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
            Check.isTrue(false, exception.toString());
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
