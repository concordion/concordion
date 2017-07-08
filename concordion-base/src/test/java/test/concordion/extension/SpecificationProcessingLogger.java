package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.listener.*;

public class SpecificationProcessingLogger implements SpecificationProcessingListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        stream.println("Before Processing " + event.getResource());
    }

    @Override
    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
        stream.println("After Processing " + event.getResource());
    }
}