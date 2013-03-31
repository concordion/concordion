package test.concordion.internal.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.Result;
import org.concordion.api.RunnerResult;
import org.concordion.api.Unimplemented;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.junit.Rule;
import org.junit.Test;

import test.concordion.ConsoleLogGobbler;
import test.concordion.StubLogger;

public class DefaultConcordionRunnerTest {

    @Rule
    public ConsoleLogGobbler logGobbler = new ConsoleLogGobbler(); // Ensure error log messages don't appear on console
    private StubLogger stubLogger = new StubLogger();

    private TestDefaultConcordionRunner runner = new TestDefaultConcordionRunner();

    @Test
    public void returnsFailureOnJUnitFailure() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, StubResult.FAILURE);
        assertThat(myresult.getResult(), is(Result.FAILURE));
    }

    @Test
    public void returnsSuccessOnJUnitSuccess() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, StubResult.SUCCESS);
        assertThat(myresult.getResult(), is(Result.SUCCESS));
    }

    // JUnit success is reported when an ExpectedToFail test does fail
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenExpectedToFail() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(ExpectedToFailClass.class, StubResult.SUCCESS);
        assertThat(myresult.getResult(), is(Result.IGNORED));
    }

    // JUnit success is reported when an Unimplemented test is unimplemented
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenUnimplemented() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnimplementedClass.class, StubResult.SUCCESS);
        assertThat(myresult.getResult(), is(Result.IGNORED));
    }

    // JUnit failure is reported when an ExpectedToFail test does not fail.
    @Test(expected = AssertionError.class)
    public void throwsAssertionErrorOnJUnitFailureWhenExpectedToFail() throws Exception {
        runner.decodeJUnitResult(ExpectedToFailClass.class, StubResult.FAILURE);
    }

    // JUnit failure is reported when an Unimplemented test is implemented
    @Test(expected = AssertionError.class)
    public void throwsAssertionErrorOnJUnitFailureWhenUnimplemented() throws Exception {
        runner.decodeJUnitResult(UnimplementedClass.class, StubResult.FAILURE);
    }

    @Test
    public void doesNotThrowExceptionOnAssertionErrorWhenExpectedToPass() throws Exception {
        Throwable error = new AssertionError();
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
        assertThat(myresult.getResult(), is(Result.FAILURE));
    }

    @Test
    public void throwsExceptionOnCheckedException() throws Exception {
        Throwable error = new IOException("dummy exception");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
            fail("expected IOException");
        } catch (IOException e) {
            assertThat(e.getMessage(), is("dummy exception"));
        }
    }

    @Test
    public void throwsWrappedExceptionOnThrowable() throws Exception {
        Throwable error = new InternalError("dummy throwable");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
            fail("expected InternalError");
        } catch (RuntimeException e) {
            assertThat(e.getCause().getMessage(), is("dummy throwable"));
        }
    }

    @Test
    public void throwsRuntimeExceptionOnRuntimeException() throws Exception {
        Throwable error = new IllegalArgumentException("dummy runtime exception");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("dummy runtime exception"));
        }
    }

    @Test
    public void returnsIgnoredOnJUnitSuccessWhenIgnoredCountGreaterThanZero() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withIgnoreCount(1));
        assertThat(myresult.getResult(), is(Result.IGNORED));
    }

    @Test
    public void logsExceptions() throws Exception {
        Throwable error = new IOException("dummy IO exception");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
        } catch (IOException e) {
        }
        assertThat(stubLogger.getNewLogMessages(), containsString("java.io.IOException: dummy IO exception"));
    }

    @Test
    public void doesNotLogAssertionErrors() throws Exception {
        Throwable error = new AssertionError("dummy assertion error");
        runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
        assertThat(stubLogger.getNewLogMessages(), is(""));
    }

    @Test
    public void doesNotLogAssertionErrorsWhenExpectedToFailEither() throws Exception {
        Throwable error = new AssertionError("dummy assertion error");
        try {
            runner.decodeJUnitResult(ExpectedToFailClass.class, new StubResult().withFailure(error));
        } catch (AssertionError e) {
        }
        assertThat(stubLogger.getNewLogMessages(), is(""));
    }

    @Test
    public void doesNotLogAssertionErrorsWhenUnimplementedEither() throws Exception {
        Throwable error = new AssertionError("dummy assertion error");
        try {
            runner.decodeJUnitResult(ExpectedToFailClass.class, new StubResult().withFailure(error));
        } catch (AssertionError e) {
        }
        assertThat(stubLogger.getNewLogMessages(), is(""));
    }

    private static final class UnannotatedClass {
    }

    @ExpectedToFail
    private static final class ExpectedToFailClass {
    }

    @Unimplemented
    private static final class UnimplementedClass {
    }

    private static final class TestDefaultConcordionRunner extends DefaultConcordionRunner {
        protected RunnerResult decodeJUnitResult(Class<?> concordionClass, org.junit.runner.Result jUnitResult) throws Exception {
            return super.decodeJUnitResult(concordionClass, jUnitResult);
        }
    }
}