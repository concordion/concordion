package org.concordion.internal.listener;

import java.io.IOException;

import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.SpecificationDescriber;
import org.concordion.internal.util.SimpleFormatter;

public class SpecificationExporter implements SpecificationProcessingListener, SpecificationDescriber {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private final Target target;

    public SpecificationExporter(Target target) {
        this.target = target;
    }
    
    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
        try {
            target.write(event.getResource(), XML_DECLARATION + event.getRootElement().toXML());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write results to '" + event.getResource().getPath() + "'.", e);
        }
    }

    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        // No action required
    }

    public String getDescription(Resource resource) {
        return target.resolvedPathFor(resource);
    }

    public String getDescription(Resource resource, String exampleName) {
        return SimpleFormatter.format("%s#%s", getDescription(resource), exampleName);
    }

}
