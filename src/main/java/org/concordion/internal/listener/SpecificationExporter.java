package org.concordion.internal.listener;

import java.io.IOException;

import org.concordion.api.Target;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.FileTarget;

public class SpecificationExporter implements SpecificationProcessingListener {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private final Target target;

    public SpecificationExporter(Target target) {
        this.target = target;
    }
    
    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
        try {
            target.write(event.getResource(), XML_DECLARATION + event.getRootElement().toXML());
            if (target instanceof FileTarget) {
                System.out.println(((FileTarget) target).getFile(event.getResource()).getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write results to '" + event.getResource().getPath() + "'.", e);
        }
    }

    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        // No action required
    }
}
