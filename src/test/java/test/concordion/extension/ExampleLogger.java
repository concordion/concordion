package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.listener.*;
import org.concordion.internal.util.SimpleFormatter;

public class ExampleLogger implements ExampleListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void beforeExample(ExampleEvent event) {
        stream.println(SimpleFormatter.format("Before example '%s'", event.getExampleName()));
    }

    @Override
    public void afterExample(ExampleEvent event) {
        stream.println(SimpleFormatter.format("After example '%s'", event.getExampleName()));
    }
}