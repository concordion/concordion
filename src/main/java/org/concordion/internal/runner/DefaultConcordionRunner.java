package org.concordion.internal.runner;

import org.concordion.api.*;
import org.concordion.internal.cache.RunResultsCache;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureType;
import org.concordion.internal.FixtureSpecificationMapper;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultConcordionRunner implements Runner {

    private static Logger logger = Logger.getLogger(DefaultConcordionRunner.class.getName());

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
        // has an opportuinity to print out any necessary debugging information.
        org.junit.runner.Result jUnitResult = runJUnitClass(fixtureClass);

        // we always decode the jUnut summary because it handles specification exceptions
        // (as opposed to exceptions that occured in a specification) better.
        ResultSummary jUnitSummary = decodeJUnitResult(new FixtureType(fixtureClass), jUnitResult);

        // check the cache - if the test was a concordion test, it will have stuck the results
        // in the cache. Use "null" for the example to get the accumulated values from all examples
        ConcordionRunOutput concordionRunOutput = cache.getFromCache(fixtureClass, null);

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

    private void processTestException(FixtureType fixtureDeclaration,
			Throwable exception) throws AssertionError, Exception {
        logExceptionIfNotAssertionError(exception);
        rethrowExceptionIfWarranted(fixtureDeclaration, exception);
	}

	protected org.junit.runner.Result runJUnitClass(Class<?> fixtureClass) {
        org.junit.runner.Result jUnitResult = JUnitCore.runClasses(fixtureClass);
        return jUnitResult;
    }

    protected ResultSummary decodeJUnitResult(FixtureType fixtureDeclaration, org.junit.runner.Result jUnitResult) throws Exception {
        Result result = Result.FAILURE;
        if (jUnitResult.wasSuccessful()) {
            result = Result.SUCCESS;
            if (onlyPartiallyImplemented(fixtureDeclaration)) {
                result = Result.IGNORED;
            }
            if (jUnitResult.getIgnoreCount() > 0) {
                result = Result.IGNORED;
            }
        } else {
            List<Failure> failures = jUnitResult.getFailures();
            for (Failure failure : failures) {
    			processTestException(fixtureDeclaration, failure.getException());
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
