package org.concordion.internal;
import org.concordion.api.Resource;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.command.ParallelRunStrategy;

//TODO rename
public class ParallelTestCompletionBlocker implements SpecificationProcessingListener {

    private final ParallelRunStrategy runStrategy;
    private static Resource mainSpec;

    public ParallelTestCompletionBlocker(ParallelRunStrategy runStrategy) {
        this.runStrategy = runStrategy;
    }
    
    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        if (mainSpec == null) {
            mainSpec = event.getResource();
//            System.out.println("main spec is " + mainSpec.getName());
            runStrategy.setMainSpecResource(mainSpec);
        }
    }

    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
//        System.out.println("Completing " + event.getResource());
        runStrategy.waitForCompletion(event.getResource());
//        System.out.println("Completed " + event.getResource());
    }
}
