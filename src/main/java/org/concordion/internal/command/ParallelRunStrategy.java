package org.concordion.internal.command;

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
import org.concordion.internal.FailFastException;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class ParallelRunStrategy implements RunStrategy {

    private class TaskCounter {
        private AtomicInteger taskCounter = new AtomicInteger();
        private Semaphore semaphore = new Semaphore(0);

        void incrementTaskCount() {
            taskCounter.incrementAndGet();
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

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private static ListeningExecutorService service = MoreExecutors.listeningDecorator(executor);
    private TaskCounter taskCounter = new TaskCounter();
    private static Resource mainSpecResource;
    
    public void setMainSpecResource(Resource mainSpec) {
        mainSpecResource = mainSpec;
    }

    public void call(final Runner runner, final Resource resource, final String href, ResultAnnouncer announcer, ResultRecorder resultRecorder) {
        try {
//            System.out.println("Submitting " + resource + " -> " + href);
            taskCounter.incrementTaskCount();
            ListenableFuture<RunnerResult> future = submitTask(new Callable<RunnerResult>() {
                public RunnerResult call() throws Exception {
                    return runner.execute(resource, href);
                }
            });
            addCallback(future, resource, announcer, resultRecorder);

        } catch (Throwable e) {
            announcer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }
    }

    public void waitForCompletion(Resource resource) {
//        System.out.println("    " + resource.getPath());
        synchronized (executor) {
            if (!resource.equals(mainSpecResource)) { 
                int newPoolSize = executor.getCorePoolSize() + 1;
                executor.setMaximumPoolSize(newPoolSize);
                executor.setCorePoolSize(newPoolSize);
            }
        }
//        System.out.println("  ==> " + executor.getCorePoolSize() + " threads -> " + resource + " waiting.");
        taskCounter.waitForAllTasksToComplete();
        synchronized (executor) {
            if (!resource.equals(mainSpecResource)) { 
                executor.setCorePoolSize(executor.getCorePoolSize() - 1);
            }
        }
//        System.out.println("  ==> " + executor.getCorePoolSize() + " threads -> " + resource + " complete.");
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
}
