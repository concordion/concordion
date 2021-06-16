package test.concordion.extension;

import java.util.List;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.OuterExampleEvent;
import org.concordion.api.listener.OuterExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.util.SimpleFormatter;

public class ExampleEventTestExtension implements ConcordionExtension, SpecificationProcessingListener, OuterExampleListener, ExampleListener {
	private String name;
	private List<String> log;
	private volatile int i;
	    
	@Override
	public void addTo(ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(this);
		concordionExtender.withOuterExampleListener(this);
		concordionExtender.withExampleListener(this);
		
	}
	
    public ExampleEventTestExtension withLog(String name, List<String> log) {
    	this.name = name;
    	this.log = log;
        return this;
    }
    
    private void log(String msg) {
        log.add(name + ": " + msg);
    }

    @Override
	public synchronized void beforeProcessingSpecification(SpecificationProcessingEvent event) {
		// FIXME remove once issue 322 is resolved
		if (log.size() < 10) {
			log("beforeProcessingSpecification");
		}
	}

	@Override
	public synchronized void afterProcessingSpecification(SpecificationProcessingEvent event) {
		log("afterProcessingSpecification");		
	}
	
    @Override
    public void beforeOuterExample(OuterExampleEvent event) {
        log(SimpleFormatter.format("Before outer example %s", event.getExampleName()));
    }

    @Override
    public void afterOuterExample(OuterExampleEvent event) {
        log(SimpleFormatter.format("After outer example %s", event.getExampleName()));
    }
    
	@Override
	public void beforeExample(ExampleEvent event) {
		log(SimpleFormatter.format("Before example %s", event.getExampleName()));
	}

	@Override
	public void afterExample(ExampleEvent event) {
		log(SimpleFormatter.format("After example %s", event.getExampleName()));
	}
}
