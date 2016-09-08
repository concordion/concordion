package org.concordion.internal.listener;

import org.concordion.api.Fixture;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.OuterExampleEvent;
import org.concordion.api.listener.OuterExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;

public class AnnounceExampleListener implements OuterExampleListener, ExampleListener {
	private final Fixture fixture;
	
	public AnnounceExampleListener(Fixture fixture) {
		this.fixture = fixture;
	}

	@Override
	public void beforeOuterExample(OuterExampleEvent event) {
		fixture.invokeBeforeExample(event.getExampleName());
	}

	@Override
	public void afterOuterExample(OuterExampleEvent event) {
		fixture.invokeAfterExample(event.getExampleName());
	}
	
	@Override
	public void beforeExample(ExampleEvent event) {
		fixture.invokeBeforeExample(event.getExampleName());
	}

	@Override
	public void afterExample(ExampleEvent event) {
		fixture.invokeAfterExample(event.getExampleName());
	}
}
