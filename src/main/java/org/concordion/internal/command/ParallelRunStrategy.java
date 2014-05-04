package org.concordion.internal.command;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Runner;
import org.concordion.api.RunnerResult;
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

public class ParallelRunStrategy implements RunStrategy, SpecificationProcessingListener {

    private static ThreadPoolExecutor executor;
    private static ListeningExecutorService service;
    private static Resource mainSpecification;
    private static Logger logger = LoggerFactory.getLogger("org.concordion.run.parallel");
    
    private TaskCounter taskCounter = new TaskCounter();
    
    public static void initialise(String runThreadCount) {
        int threadPoolSize = parseThreadCount(runThreadCount);
        logger.info("Running concordion:run commands in parallel with {} threads\n", threadPoolSize);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
        service = MoreExecutors.listeningDecorator(executor);
    }

    public void call(final Runner runner, final Resource resource, final String href, ResultAnnouncer announcer, ResultRecorder resultRecorder) {
        try {
            logger.debug("Submit: {} -> {}", resource, href);
            taskCounter.incrementTaskCount();
            ListenableFuture<RunnerResult> future = submitTask(new Callable<RunnerResult>() {
                public RunnerResult call() throws Exception {
                    logger.debug("Start: {} -> {}", resource, href);
                    try {
                        return runner.execute(resource, href);
                    } finally {
                        logger.debug("Finish: {} -> {}", resource, href);
                    }
                }
            });
            addCallback(future, resource, announcer, resultRecorder);

        } catch (Throwable e) {
            announcer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }
    }

    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        if (mainSpecification == null) {
            mainSpecification = event.getResource();
        }
    }

    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
        waitForCompletion(event.getResource());
    }
    
    private static int parseThreadCount(String threadCount) {
        try {
            if (threadCount.endsWith("C")) {
                return new BigDecimal(threadCount.substring(0, threadCount.length() - 1)).multiply(new BigDecimal(Runtime.getRuntime().availableProcessors())).intValue();
            } 
            return Integer.valueOf(threadCount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The system property '" + ConcordionBuilder.PROPERTY_RUN_THREAD_COUNT 
                    + "' must set to either an integer value, or a numeric value suffixed with C."
                    + " If the latter, the numeric value is multiplied by the number of cores.");
        }
    }
    
    private void waitForCompletion(Resource resource) {
        if (taskCounter.hasTasksToComplete()) {
            // to avoid thread starvation when this thread blocks waiting for its tasks to complete, allocate an extra thread 
            allocateWaitThread(resource);
            taskCounter.waitForAllTasksToComplete();
            deallocateWaitThread(resource);
        }
    }

    private void allocateWaitThread(Resource resource) {
        synchronized (executor) {
            if (!resource.equals(mainSpecification)) { 
                int newPoolSize = executor.getCorePoolSize() + 1;
                executor.setMaximumPoolSize(newPoolSize);
                executor.setCorePoolSize(newPoolSize);
            }
        }
        logger.debug("Wait: {}. Total threads: {}", resource, executor.getCorePoolSize());

    }

    private void deallocateWaitThread(Resource resource) {
        synchronized (executor) {
            if (!resource.equals(mainSpecification)) { 
                int newPoolSize = executor.getCorePoolSize() - 1;
                executor.setCorePoolSize(newPoolSize);
                executor.setMaximumPoolSize(newPoolSize);
            }
        }
        logger.debug("Complete: {}. Total threads: {}", resource, executor.getCorePoolSize());
    }
    
    private ListenableFuture<RunnerResult> submitTask(Callable<RunnerResult> task) {
        return service.submit(task);
    }
    
    private void addCallback(ListenableFuture<RunnerResult> future, final Resource resource, final ResultAnnouncer announcer, final ResultRecorder resultRecorder) {
        Futures.addCallback(future, new FutureCallback<RunnerResult>() {

            @Override
            public void onSuccess(RunnerResult runnerResult) {
                Result result = runnerResult.getResult();
                announcer.announce(result);
                resultRecorder.record(result);
                taskCounter.completedTask();
            }

            @Override
            public void onFailure(Throwable t) {
                if (t.getCause() instanceof FailFastException) {
                    announcer.announce(Result.FAILURE);
                    resultRecorder.record(Result.FAILURE);
                } else {
                    announcer.announceException(t);
                    resultRecorder.record(Result.EXCEPTION);
                }
                taskCounter.completedTask();
            }
        });
    }
    
    private class TaskCounter {
        private AtomicInteger taskCounter = new AtomicInteger();
        private Semaphore semaphore = new Semaphore(0);
        
        void incrementTaskCount() {
            taskCounter.incrementAndGet();
        }
        
        boolean hasTasksToComplete() {
            return taskCounter.get() > 0;
        }
        
        void completedTask() {
            semaphore.release(1);
        }
        
        void waitForAllTasksToComplete() {
            try {
                int taskCount = taskCounter.get();
                semaphore.acquire(taskCount);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

