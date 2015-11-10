package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.listener.*;

public class ExampleLogger implements ExampleListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void beforeExample(ExampleEvent event) {
        stream.println(String.format("Before example '%s'", event.getExampleName()));
    }

    @Override
    public void afterExample(ExampleEvent event) {
        stream.println(String.format("After example '%s'", event.getExampleName()));
    }
}