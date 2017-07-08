package org.concordion.api.listener;

import java.util.EventListener;

public interface SpecificationProcessingListener extends EventListener {

    void beforeProcessingSpecification(SpecificationProcessingEvent event);

    void afterProcessingSpecification(SpecificationProcessingEvent event);
}
