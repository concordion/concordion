package org.concordion.api.listener;

import org.concordion.api.Element;
import org.concordion.api.Resource;

public class SpecificationProcessingEvent {

    private final Element rootElement;
    private final Resource resource;

    public SpecificationProcessingEvent(Resource resource, Element rootElement) {
        this.resource = resource;
        this.rootElement = rootElement;
    }

    public Element getRootElement() {
        return rootElement;
    }

    public Resource getResource() {
        return resource;
    }
}
