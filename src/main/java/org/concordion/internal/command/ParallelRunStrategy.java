package org.concordion.internal.command;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;
import org.concordion.api.Runner;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.FailFastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Runs specifications in parallel. Each specification fixture has its own instance of ParallelRunStrategy.
 * The {@link #call(Runner, Resource, String, ResultAnnouncer, ResultRecorder)} method is invoked
 * for each child specification that the fixture executes.
 * (this is called from the RunCommand when a concordion:run command is executed).
 *
 * After processing a specification, this class waits for all of the executed child specifications to complete.
 * To avoid thread starvation, it allocates an additional thread while waiting.
 *
 * The thread pool is shared across all instances.
 *
 * Before usage, the {@link #initialise(String)} method must be called to initialise the thread pool to the
 * appropriate size.
 */
public class ParallelRunStrategy implements RunStrategy, SpecificationProcessingListener {

    private static ThreadPoolExecutor executor;
    private static ListeningExecutorService service;
    private static Object poolSizeLock = new Object();
    private static volatile Resource mainSpecification;
    private static Logger logger = LoggerFactory.getLogger("org.concordion.run.parallel");

    private final TaskLatch taskLatch = new TaskLatch();

    /**
     * Initialises the thread pool.
     * @param runThreadCount the number of threads in the thread pool. If this ends with C, the runThreadCount is
     * multiplied by the number of cores. For example, a runThreadCount of <code>2.5C</code> will allocate 10
     * threads when there are 4 processors available to the JVM.
     */
    public static void initialise(final String runThreadCount) {
        final int threadPoolSize = parseThreadCount(runThreadCount);
        logger.info("Running concordion:run commands in parallel with {} threads\n", threadPoolSize);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
        service = MoreExecutors.listeningDecorator(executor);
    }

    /**
     * Runs a "child" specification.
     * @param runner  the Concordion runner to use
     * @param resource the current specification resource
     * @param href the URL of the child specification to run. This is normally relative to the current specification resource
     * @param announcer announces the results to all listeners (eg. listeners that update the results in the output specification)
     * @param resultRecorder records the results (eg. for console output)
     */
    @Override
	public void call(final Runner runner, final Resource resource, final String href, final ResultAnnouncer announcer, final ResultRecorder resultRecorder) {
        try {
            logger.debug("Submit: {} -> {}", resource, href);
            taskLatch.registerTask();
            final ListenableFuture<ResultSummary> future = submitTask(createTask(runner, resource, href));
            addCallback(future, resource, announcer, resultRecorder);

        } catch (final Throwable e) {
            announcer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }
    }

    @Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
        if (mainSpecification == null) {
            mainSpecification = event.getResource();
        }
    }

    @Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
        waitForCompletion(event.getResource());
    }

    private static int parseThreadCount(final String threadCount) {
        try {
            if (threadCount.endsWith("C")) {
                return new BigDecimal(threadCount.substring(0, threadCount.length() - 1)).multiply(new BigDecimal(Runtime.getRuntime().availableProcessors())).intValue();
            }
            return Integer.parseInt(threadCount);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("The system property '" + ConcordionBuilder.PROPERTY_RUN_THREAD_COUNT
                    + "' must set to either an integer value, or a numeric value suffixed with C."
                    + " If the latter, the numeric value is multiplied by the number of cores.");
        }
    }

    private Callable<ResultSummary> createTask(final Runner runner, final Resource resource, final String href) {
        return new Callable<ResultSummary>() {
            @Override
			public ResultSummary call() throws Exception {
                logger.debug("Start: {} -> {}", resource, href);
                try {
                    return runner.execute(resource, href);
                } finally {
                    logger.debug("Finish: {} -> {}", resource, href);
                }
            }
        };
    }

    private ListenableFuture<ResultSummary> submitTask(final Callable<ResultSummary> task) {
        return service.submit(task);
    }

    private void addCallback(final ListenableFuture<ResultSummary> future, final Resource resource, final ResultAnnouncer announcer, final ResultRecorder resultRecorder) {
        Futures.addCallback(future, new FutureCallback<ResultSummary>() {

            @Override
            public void onSuccess(final ResultSummary runnerResult) {
                announcer.announce(runnerResult);
                resultRecorder.record(runnerResult);
                taskLatch.markTaskComplete();
            }

            @Override
            public void onFailure(final Throwable t) {
                if (t.getCause() instanceof FailFastException) {
                    announcer.announce(new SingleResultSummary(Result.FAILURE));
                    resultRecorder.record(Result.FAILURE);
                } else {
                    announcer.announceException(t);
                    resultRecorder.record(Result.EXCEPTION);
                }
                taskLatch.markTaskComplete();
            }
        });
    }

    private void waitForCompletion(final Resource resource) {
        if (taskLatch.hasRegisteredTasks()) {
            // to avoid thread starvation when this thread blocks waiting for its tasks to complete, allocate an extra thread
            allocateWaitThread(resource);
            taskLatch.waitForAllTasksToComplete();
            deallocateWaitThread(resource);
        }
    }

    private void allocateWaitThread(final Resource resource) {
        synchronized (poolSizeLock) {
            if (!resource.equals(mainSpecification)) {
                final int newPoolSize = executor.getCorePoolSize() + 1;
                executor.setMaximumPoolSize(newPoolSize);
                executor.setCorePoolSize(newPoolSize);
            }
        }
        logger.debug("Wait: {}. Total threads: {}", resource, executor.getCorePoolSize());
    }

    private void deallocateWaitThread(final Resource resource) {
        synchronized (poolSizeLock) {
            if (!resource.equals(mainSpecification)) {
                final int newPoolSize = executor.getCorePoolSize() - 1;
                executor.setCorePoolSize(newPoolSize);
                executor.setMaximumPoolSize(newPoolSize);
            }
        }
        logger.debug("Complete: {}. Total threads: {}", resource, executor.getCorePoolSize());
    }

    /**
     * A latch to wait for tasks to complete.
     * This proceeds in 2 distinct phases:
     * 1. New tasks are registered, using {@link #registerTask()}
     * 2. After all tasks are registered, task completion is awaited using {@link #waitForAllTasksToComplete()}
     * {@link #markTaskComplete()} may be called at any time to mark a task as complete.
     *
     * {@link #waitForAllTasksToComplete()} for wait until {@link #markTaskComplete()} has been called the same
     * number of times as {@link #registerTask()}. Before waiting, {@link #hasRegisteredTasks()} may be called
     * to check whether any tasks were registered.
     *
     * This class is thread-safe.
     *
     * In Java 7, the Phaser class would be a replacement for this (see http://stackoverflow.com/a/1637030).
     */
    private static class TaskLatch {
        private final AtomicInteger taskCounter = new AtomicInteger(0);
        private final AtomicBoolean waiting = new AtomicBoolean(false);
        private final Semaphore semaphore = new Semaphore(0);

        void registerTask() {
            if (waiting.get()) {
                throw new IllegalStateException("New tasks not expected when waiting.");
            }
            taskCounter.incrementAndGet();
        }

        boolean hasRegisteredTasks() {
            waiting.set(true);
            return taskCounter.get() > 0;
        }

        void markTaskComplete() {
            semaphore.release(1);
        }

        void waitForAllTasksToComplete() {
            waiting.set(true);
            boolean complete = false;
            final int taskCount = taskCounter.get();
            while (!complete) {
                try {
                    semaphore.acquire(taskCount);
                    complete = true;
                } catch (final InterruptedException e) {
                    logger.debug("Interrupted while waiting for tasks to complete");
                }
            }
        }
    }
}

