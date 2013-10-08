package org.concordion.internal.runner;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.ParallelRunner;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.RunnerResult;
import org.concordion.api.Unimplemented;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

public class DefaultConcordionParallelRunner implements ParallelRunner {

    private static Logger logger = Logger.getLogger(DefaultConcordionParallelRunner.class.getName());
    
    public Callable<RunnerResult> createRunnerTask(Resource resource, String href) throws Exception {
        final Class<?> concordionClass = findTestClass(resource, href);
        return new Callable<RunnerResult>() {
            public RunnerResult call() throws Exception {
                org.junit.runner.Result jUnitResult = JUnitCore.runClasses(concordionClass);
                return decodeJUnitResult(concordionClass, jUnitResult );
            }
        };
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
            concordionClass = Class.forName(name + "Test");
        }
        return concordionClass;
    }

    protected RunnerResult decodeJUnitResult(Class<?> concordionClass, org.junit.runner.Result jUnitResult)
            throws Exception {
        Result result = Result.FAILURE;
        if (jUnitResult.wasSuccessful()) {
            result = Result.SUCCESS;
            if (isOnlySuccessfulBecauseItWasExpectedToFail(concordionClass)
             || isOnlySuccessfulBecauseItIsUnimplemented(concordionClass)) {
                result = Result.IGNORED;
            }
            if (jUnitResult.getIgnoreCount() > 0) {
                result = Result.IGNORED;
            }
        } else {
            List<Failure> failures = jUnitResult.getFailures();
            for (Failure failure : failures) {
                Throwable exception = failure.getException();
                if (!(exception instanceof AssertionError)) {
                    logger.log(Level.WARNING, "", exception);
                    if (exception instanceof Exception) {
                        throw (Exception)exception;
                    } else {
                        throw new RuntimeException(exception);
                    }
                }
            }
        }
        return new RunnerResult(result);
    }

    private boolean isOnlySuccessfulBecauseItWasExpectedToFail(Class<?> concordionClass) {
        return concordionClass.getAnnotation(ExpectedToFail.class) != null;
    }
    
    private boolean isOnlySuccessfulBecauseItIsUnimplemented(Class<?> concordionClass) {
        return concordionClass.getAnnotation(Unimplemented.class) != null;
    }
}
