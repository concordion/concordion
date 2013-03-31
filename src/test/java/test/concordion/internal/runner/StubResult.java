package test.concordion.internal.runner;

import java.util.ArrayList;
import java.util.List;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Stub for {@link Result}. Defaults to a successful result.
 */
public class StubResult extends Result {

    private static final long serialVersionUID = 6102153773248794468L;
    public static final StubResult SUCCESS = new StubResult();
    public static final StubResult FAILURE = new StubResult().withFailure(new AssertionError("Dummy test failure"));

    private List<Failure> failures = new ArrayList<Failure>();
    private int ignoreCount;

    public StubResult withFailure(Throwable error) {
        failures.add(new Failure(Description.EMPTY, error));
        return this;
    }

    public StubResult withIgnoreCount(int ignoreCount) {
        this.ignoreCount = ignoreCount;
        return this;
    }

    @Override
    public boolean wasSuccessful() {
        return failures.size() == 0;
    }

    @Override
    public List<Failure> getFailures() {
        return failures;
    }

    @Override
    public int getIgnoreCount() {
        return ignoreCount;
    }
}