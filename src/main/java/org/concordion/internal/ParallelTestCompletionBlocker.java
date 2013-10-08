package org.concordion.internal;
import java.util.concurrent.ThreadPoolExecutor;

import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.command.ParallelRunCommand;


public class ParallelTestCompletionBlocker implements SpecificationProcessingListener {

    private final ParallelRunCommand runCommand;
    private static String mainSpec;

    public ParallelTestCompletionBlocker(ParallelRunCommand runCommand) {
        this.runCommand = runCommand;
    }
    
    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        if (mainSpec == null) {
            mainSpec = event.getResource().getName();
            System.out.println("main spec is " + mainSpec);
        }
    }

    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
        ThreadPoolExecutor executor = ParallelRunCommand.threadLocalExecutor.get();

        //TODO dodgy code!!  Since this is only method updating pool size, the operations are atomic.  
        //Possible issues with decreasing pool size, since it will set interrupt flag on one thread. Would be better if there was a way to tell it to decrement pool by this thread when it finishes.
        synchronized (executor) {
            if (!event.getResource().getName().equals(mainSpec)) {
//                if (!event.getResource().getName().equals("testrig")) {
//                    System.out.println(event.getResource() + " pool++ = " + executor.getCorePoolSize());
//                    System.out.flush();
                    executor.setCorePoolSize(executor.getCorePoolSize() + 1);
//                }
            }
        }
        runCommand.waitForCompletion(event.getResource());
        synchronized (executor) {
            if (!event.getResource().getName().equals(mainSpec)) {
//                if (!event.getResource().getName().equals("testrig")) {
                    executor.setCorePoolSize(executor.getCorePoolSize() - 1);
//                    System.out.println(event.getResource() + " pool-- = " + executor.getCorePoolSize());
//                    System.out.flush();
//                }
            }
        }
    }
}
