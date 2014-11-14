package org.concordion.internal.runner;

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
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.FailFastException;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.SummarizingResultRecorder;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

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
    	// check for a jUnit 3 style test extension
       	if (ConcordionTestCase.class.isAssignableFrom(concordionClass)) {
    		return runConcordionTest(concordionClass);
    	}
       	
       	// check for a jUnit 4 style annotation
       	if (concordionClass.isAnnotationPresent(RunWith.class)) {
       		RunWith s = concordionClass.getAnnotation(RunWith.class);
       		if (s.value().isAssignableFrom(ConcordionRunner.class)) {
       			return runConcordionTest(concordionClass);
       		}
       	}
 
    	
    	org.junit.runner.Result jUnitResult = runJUnitClass(concordionClass);
        return decodeJUnitResult(concordionClass, jUnitResult);
    }

    private ResultSummary runConcordionTest(Class<?> concordionClass) throws Exception {
    	Object o = concordionClass.getConstructor((Class<?>[])null).newInstance((Object[])null);
    	return new FixtureRunner().run(o);
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
                Throwable exception = failure.getException();
                logExceptionIfNotAssertionError(exception);
                rethrowExceptionIfWarranted(concordionClass, exception);
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
