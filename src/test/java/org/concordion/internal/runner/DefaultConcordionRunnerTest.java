package org.concordion.internal.runner;

import java.io.IOException;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.ResultSummary;
import org.concordion.api.Unimplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import static org.junit.Assert.*;

public class DefaultConcordionRunnerTest {

    @Rule
    public final SystemErrRule systemErrRule = 
        new SystemErrRule().enableLog().muteForSuccessfulTests(); // Ensure error log messages don't appear on console

    private final DefaultConcordionRunner runner = new DefaultConcordionRunner();

    @Test
    public void returnsFailureOnJUnitFailure() throws Exception {
        ResultSummary myresult = runner.decodeJUnitPlatformEngineResult(
                StubTestExecutionSummary.FAILURE, UnannotatedClass.class);
        assertEquals(1, myresult.getFailureCount());
        assertEquals(0, myresult.getIgnoredCount());
        assertEquals(0, myresult.getExceptionCount());
        assertEquals(0, myresult.getSuccessCount());
    }

    @Test
    public void returnsSuccessOnJUnitSuccess() throws Exception {
        ResultSummary myresult = runner.decodeJUnitPlatformEngineResult(
                StubTestExecutionSummary.SUCCESS, UnannotatedClass.class);
        assertEquals(0, myresult.getFailureCount());
        assertEquals(0, myresult.getIgnoredCount());
        assertEquals(0, myresult.getExceptionCount());
        assertEquals(1, myresult.getSuccessCount());
    }

    // JUnit success is reported when an ExpectedToFail test does fail
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenExpectedToFail() throws Exception {
        ResultSummary myresult = runner.decodeJUnitPlatformEngineResult(
                StubTestExecutionSummary.SUCCESS, ExpectedToFailClass.class);
        assertEquals(0, myresult.getFailureCount());
        assertEquals(1, myresult.getIgnoredCount());
        assertEquals(0, myresult.getExceptionCount());
        assertEquals(0, myresult.getSuccessCount());
   }

    // JUnit success is reported when an Unimplemented test is unimplemented
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenUnimplemented() throws Exception {
        ResultSummary myresult = runner.decodeJUnitPlatformEngineResult(
                StubTestExecutionSummary.SUCCESS, UnimplementedClass.class);
        assertEquals(0, myresult.getFailureCount());
        assertEquals(1, myresult.getIgnoredCount());
        assertEquals(0, myresult.getExceptionCount());
        assertEquals(0, myresult.getSuccessCount());
    }

    // JUnit failure is reported when an ExpectedToFail test does not fail.
    @Test(expected = AssertionError.class)
    public void throwsAssertionErrorOnJUnitFailureWhenExpectedToFail() throws Exception {
        runner.decodeJUnitPlatformEngineResult(StubTestExecutionSummary.FAILURE, ExpectedToFailClass.class);
    }

    // JUnit failure is reported when an Unimplemented test is implemented
    @Test(expected = AssertionError.class)
    public void throwsAssertionErrorOnJUnitFailureWhenUnimplemented() throws Exception {
        runner.decodeJUnitPlatformEngineResult(StubTestExecutionSummary.FAILURE, UnimplementedClass.class);
    }

    @Test
    public void doesNotThrowExceptionOnAssertionErrorWhenExpectedToPass() throws Exception {
        Throwable error = new AssertionError();
        ResultSummary myresult = runner.decodeJUnitPlatformEngineResult(
                new StubTestExecutionSummary()
                        .withTestsFoundCount(1)
                        .withTestsStartedCount(1)
                        .withTestsFailedCount(1)
                        .withFailure(error),
                UnannotatedClass.class);
        assertEquals(1, myresult.getFailureCount());
        assertEquals(0, myresult.getIgnoredCount());
        assertEquals(0, myresult.getExceptionCount());
        assertEquals(0, myresult.getSuccessCount());
    }

    @Test
    public void throwsExceptionOnCheckedException() throws Exception {
        Throwable error = new IOException("dummy exception");
        try {
            runner.decodeJUnitPlatformEngineResult(
                    new StubTestExecutionSummary()
                            .withTestsFoundCount(1)
                            .withTestsStartedCount(1)
                            .withTestsFailedCount(1)
                            .withFailure(error),
                    UnannotatedClass.class);
            fail("expected IOException");
        } catch (IOException e) {
            assertEquals("dummy exception", e.getMessage());
        }
    }

    @Test
    public void throwsWrappedExceptionOnThrowable() throws Exception {
        Throwable error = new InternalError("dummy throwable");
        try {
            runner.decodeJUnitPlatformEngineResult(
                    new StubTestExecutionSummary()
                            .withTestsFoundCount(1)
                            .withTestsStartedCount(1)
                            .withTestsFailedCount(1)
                            .withFailure(error),
                    UnannotatedClass.class);
            fail("expected InternalError");
        } catch (RuntimeException e) {
            assertEquals("dummy throwable", e.getCause().getMessage());
        }
    }

    @Test
    public void throwsRuntimeExceptionOnRuntimeException() throws Exception {
        Throwable error = new IllegalArgumentException("dummy runtime exception");
        try {
            runner.decodeJUnitPlatformEngineResult(
                    new StubTestExecutionSummary()
                            .withTestsFoundCount(1)
                            .withTestsStartedCount(1)
                            .withTestsFailedCount(1)
                            .withFailure(error),
                    UnannotatedClass.class);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("dummy runtime exception", e.getMessage());
        }
    }

    @Test
    public void returnsIgnoredOnJUnitSuccessWhenIgnoredCountGreaterThanZero() throws Exception {
        ResultSummary myresult = runner.decodeJUnitPlatformEngineResult(
                new StubTestExecutionSummary()
                        .withTestsFoundCount(1)
                        .withTestsSkippedCount(1),
                UnannotatedClass.class);
        assertEquals(0, myresult.getFailureCount());
        assertEquals(1, myresult.getIgnoredCount());
        assertEquals(0, myresult.getExceptionCount());
        assertEquals(0, myresult.getSuccessCount());
    }

    @Test
    public void logsExceptions() throws Exception {
        Throwable error = new IOException("dummy IO exception");
        try {
            runner.decodeJUnitPlatformEngineResult(
                    new StubTestExecutionSummary()
                            .withTestsFoundCount(1)
                            .withTestsStartedCount(1)
                            .withTestsFailedCount(1)
                            .withFailure(error),
                    UnannotatedClass.class);
        } catch (IOException e) {
        }
        assertTrue(systemErrRule.getLog().contains("java.io.IOException: dummy IO exception"));
    }

    @Test
    public void doesNotLogAssertionErrors() throws Exception {
        Throwable error = new AssertionError("dummy assertion error");
        runner.decodeJUnitPlatformEngineResult(
                new StubTestExecutionSummary()
                        .withTestsFoundCount(1)
                        .withTestsStartedCount(1)
                        .withTestsFailedCount(1)
                        .withFailure(error),
                UnannotatedClass.class);
        assertEquals("", systemErrRule.getLog());
    }

    @Test
    public void doesNotLogAssertionErrorsWhenExpectedToFailEither() throws Exception {
        Throwable error = new AssertionError("dummy assertion error");
        try {
            runner.decodeJUnitPlatformEngineResult(
                    new StubTestExecutionSummary()
                            .withTestsFoundCount(1)
                            .withTestsStartedCount(1)
                            .withTestsFailedCount(1)
                            .withFailure(error),
                    ExpectedToFailClass.class);
        } catch (AssertionError e) {
        }
        assertEquals("", systemErrRule.getLog());
    }

    @Test
    public void doesNotLogAssertionErrorsWhenUnimplementedEither() throws Exception {
        Throwable error = new AssertionError("dummy assertion error");
        try {
            runner.decodeJUnitPlatformEngineResult(
                    new StubTestExecutionSummary()
                            .withTestsFoundCount(1)
                            .withTestsStartedCount(1)
                            .withTestsFailedCount(1)
                            .withFailure(error),
                    UnimplementedClass.class);
        } catch (AssertionError e) {
        }
        assertEquals("", systemErrRule.getLog());
    }

    private static final class UnannotatedClass {
    }

    @ExpectedToFail
    private static final class ExpectedToFailClass {
    }

    @Unimplemented
    private static final class UnimplementedClass {
    }

}