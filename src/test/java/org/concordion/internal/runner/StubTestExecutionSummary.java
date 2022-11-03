package org.concordion.internal.runner;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class StubTestExecutionSummary implements TestExecutionSummary {

	public static final StubTestExecutionSummary FAILURE = new StubTestExecutionSummary()
			.withContainersFoundCount(1)
			.withContainersStartedCount(1)
			.withTestsFoundCount(1)
			.withTestsStartedCount(1)
			.withTestsFailedCount(1)
			.withFailure(new AssertionError("Stub exception"));
	public static final StubTestExecutionSummary SUCCESS = new StubTestExecutionSummary()
			.withContainersFoundCount(1)
			.withContainersStartedCount(1)
			.withTestsFoundCount(1)
			.withTestsStartedCount(1)
			.withTestsSuccededCount(1);

	private long timeStarted = 0;
	private long timeFinished = 0;

	private long containersFoundCount = 0;
	private long containersStartedCount = 0;
	private long containersSkippedCount = 0;
	private long containersAbortedCount = 0;
	private long containersSucceededCount = 0;
	private long containersFailedCount = 0;

	private long testsFoundCount = 0;
	private long testsStartedCount = 0;
	private long testsSkippedCount = 0;
	private long testsAbortedCount = 0;
	private long testsSucceededCount = 0;
	private long testsFailedCount = 0;

	private List<Failure> failures = new ArrayList<>();

	@Override
	public long getTimeStarted() {
		return timeStarted;
	}

	public StubTestExecutionSummary withTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
		return this;
	}

	@Override
	public long getTimeFinished() {
		return timeFinished;
	}

	public StubTestExecutionSummary withTimeFinished(long timeFinished) {
		this.timeFinished = timeFinished;
		return this;
	}

	@Override
	public long getTotalFailureCount() {
		return getTestsFailedCount() + getContainersFailedCount();
	}

	@Override
	public long getContainersFoundCount() {
		return containersFoundCount;
	}

	public StubTestExecutionSummary withContainersFoundCount(long containersFoundCount) {
		this.containersFoundCount = containersFoundCount;
		return this;
	}

	@Override
	public long getContainersStartedCount() {
		return containersStartedCount;
	}

	public StubTestExecutionSummary withContainersStartedCount(long containersStartedCount) {
		this.containersStartedCount = containersStartedCount;
		return this;
	}

	@Override
	public long getContainersSkippedCount() {
		return containersSkippedCount;
	}

	public StubTestExecutionSummary withContainersSkippedCount(long containersSkippedCount) {
		this.containersSkippedCount = containersSkippedCount;
		return this;
	}

	@Override
	public long getContainersAbortedCount() {
		return containersAbortedCount;
	}

	public StubTestExecutionSummary withContainersAbortedCount(long containersAbortedCount) {
		this.containersAbortedCount = containersAbortedCount;
		return this;
	}

	@Override
	public long getContainersSucceededCount() {
		return containersSucceededCount;
	}

	public StubTestExecutionSummary withContainersSucceededCount(long containersSucceededCount) {
		this.containersSucceededCount = containersSucceededCount;
		return this;
	}

	@Override
	public long getContainersFailedCount() {
		return containersFailedCount;
	}

	public StubTestExecutionSummary withContainersFailedCount(long containersFailedCount) {
		this.containersFailedCount = containersFailedCount;
		return this;
	}

	@Override
	public long getTestsFoundCount() {
		return testsFoundCount;
	}

	public StubTestExecutionSummary withTestsFoundCount(long testsFoundCount) {
		this.testsFoundCount = testsFoundCount;
		return this;
	}

	@Override
	public long getTestsStartedCount() {
		return testsStartedCount;
	}

	public StubTestExecutionSummary withTestsStartedCount(long testsStartedCount) {
		this.testsStartedCount = testsStartedCount;
		return this;
	}

	@Override
	public long getTestsSkippedCount() {
		return testsSkippedCount;
	}

	public StubTestExecutionSummary withTestsSkippedCount(long testsSkippedCount) {
		this.testsSkippedCount = testsSkippedCount;
		return this;
	}

	@Override
	public long getTestsAbortedCount() {
		return testsAbortedCount;
	}

	public StubTestExecutionSummary withTestsAbortedCount(long testsAbortedCount) {
		this.testsAbortedCount = testsAbortedCount;
		return this;
	}

	@Override
	public long getTestsSucceededCount() {
		return testsSucceededCount;
	}

	public StubTestExecutionSummary withTestsSuccededCount(long testsSucceededCount) {
		this.testsSucceededCount = testsSucceededCount;
		return this;
	}

	@Override
	public long getTestsFailedCount() {
		return testsFailedCount;
	}

	public StubTestExecutionSummary withTestsFailedCount(long testsFailedCount) {
		this.testsFailedCount = testsFailedCount;
		return this;
	}

	@Override
	public void printTo(PrintWriter writer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void printFailuresTo(PrintWriter writer) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Failure> getFailures() {
		return failures;
	}

	public StubTestExecutionSummary withFailure(Throwable error) {
		failures.add(new StubFailure(null, error));
		return this;
	}

	@SuppressWarnings("serial")
	private static class StubFailure implements Failure {

		private final TestIdentifier testIdentifier;
		private final Throwable exception;

		public StubFailure(TestIdentifier testIdentifier, Throwable exception) {
			super();
			this.testIdentifier = testIdentifier;
			this.exception = exception;
		}

		@Override
		public TestIdentifier getTestIdentifier() {
			return testIdentifier;
		}

		@Override
		public Throwable getException() {
			return exception;
		}
		
	}

}
