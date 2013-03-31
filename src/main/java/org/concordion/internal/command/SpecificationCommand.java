package org.concordion.internal.command;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Resource;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.FailFastException;
import org.concordion.internal.util.Announcer;

public class SpecificationCommand extends AbstractCommand {

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        throw new IllegalStateException("Unexpected call to " + getClass().getSimpleName() + "'s setUp() method. Only the execute() method should be called.");
    }
    
    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        announceBeforeProcessingEvent(commandCall.getResource(), commandCall.getElement());
        try {
            commandCall.getChildren().processSequentially(evaluator, resultRecorder);
        } catch (FailFastException e) {
            // Ignore
        }
        announceAfterProcessingEvent(commandCall.getResource(), commandCall.getElement());
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        throw new IllegalStateException("Unexpected call to " + getClass().getSimpleName() + "'s verify() method. Only the execute() method should be called.");
    }

    private Announcer<SpecificationProcessingListener> listeners = Announcer.to(SpecificationProcessingListener.class);
    
    public void addSpecificationListener(SpecificationProcessingListener listener) {
        listeners.addListener(listener);
    }

    public void removeSpecificationListener(SpecificationProcessingListener listener) {
        listeners.removeListener(listener);
    }

    private void announceAfterProcessingEvent(Resource resource, Element element) {
        listeners.announce().afterProcessingSpecification(new SpecificationProcessingEvent(resource, element));
    }

    private void announceBeforeProcessingEvent(Resource resource, Element element) {
        listeners.announce().beforeProcessingSpecification(new SpecificationProcessingEvent(resource, element));
    }
}
