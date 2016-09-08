package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.OuterExampleEvent;
import org.concordion.api.listener.OuterExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.FailFastException;
import org.concordion.internal.SpecificationDescriber;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.listener.SpecificationExporter;

import static org.concordion.internal.XMLSpecification.OUTER_EXAMPLE_NAME;

public class SpecificationCommand extends AbstractCommand {

    private List<SpecificationProcessingListener> listeners = new ArrayList<SpecificationProcessingListener>();
    private List<OuterExampleListener> outerExampleListeners = new ArrayList<OuterExampleListener>();
    private SpecificationDescriber specificationDescriber;

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        throw new IllegalStateException("Unexpected call to " + getClass().getSimpleName() + "'s setUp() method. Only the execute() method should be called.");
    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        if (specificationDescriber != null) {
            resultRecorder.setSpecificationDescription(getSpecificationDescription(commandCall));
        }

        try {
            announceBeforeOuterExampleEvent(commandCall.getElement(), (SummarizingResultRecorder) resultRecorder);
            commandCall.getChildren().processSequentially(evaluator, resultRecorder);
        } catch (FailFastException e) {
            // Ignore - it'll be re-thrown later if necessary.
        } finally {
            announceAfterOuterExampleEvent(commandCall.getElement(), (SummarizingResultRecorder) resultRecorder);
        }
    }

    public String getSpecificationDescription(CommandCall commandCall) {
        return specificationDescriber.getDescription(commandCall.getResource());
    }

    public void start(CommandCall commandCall) {
        announceBeforeProcessingEvent(commandCall.getResource(), commandCall.getElement());
    }

    public void finish(CommandCall commandCall) {
        announceAfterProcessingEvent(commandCall.getResource(), commandCall.getElement());
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        throw new IllegalStateException("Unexpected call to " + getClass().getSimpleName() + "'s verify() method. Only the execute() method should be called.");
    }

    public void addSpecificationListener(SpecificationProcessingListener listener) {
        listeners.add(listener);
    }

    public void removeSpecificationListener(SpecificationProcessingListener listener) {
        listeners.remove(listener);
    }

    public void addOuterExampleListener(OuterExampleListener listener) {
        outerExampleListeners.add(listener);
    }

    public void removeOuterExampleListener(OuterExampleListener listener) {
        outerExampleListeners.remove(listener);
    }

    public void setSpecificationDescriber(SpecificationDescriber specificationDescriber) {
        this.specificationDescriber = specificationDescriber;
    }

    private void announceAfterProcessingEvent(Resource resource, Element element) {
    	SpecificationExporter exporter = null;
    	
    	for (int i = listeners.size() - 1; i >= 0; i--) {
    		SpecificationProcessingListener listener = (SpecificationProcessingListener) listeners.get(i);
    		
    		if (listener instanceof SpecificationExporter) {
    			exporter = (SpecificationExporter) listener;
    		} else {
    			listener.afterProcessingSpecification(new SpecificationProcessingEvent(resource, element));
    		}
    	}
    	
    	if (exporter != null) {
    		exporter.afterProcessingSpecification(new SpecificationProcessingEvent(resource, element));
    	}
    }

    private void announceBeforeProcessingEvent(Resource resource, Element element) {
    	for (SpecificationProcessingListener listener : listeners) {
    		listener.beforeProcessingSpecification(new SpecificationProcessingEvent(resource, element));
		}
    }

    private void announceBeforeOuterExampleEvent(Element element, ResultSummary resultSummary) {
        for (OuterExampleListener listener : outerExampleListeners) {
            listener.beforeOuterExample(new OuterExampleEvent(OUTER_EXAMPLE_NAME, element, resultSummary));
        }
    }

    private void announceAfterOuterExampleEvent(Element element, ResultSummary resultSummary) {
    	for (int i = outerExampleListeners.size() - 1; i >= 0; i--) {
    		outerExampleListeners.get(i).afterOuterExample(new OuterExampleEvent(OUTER_EXAMPLE_NAME, element, resultSummary));
        }
    }
}
