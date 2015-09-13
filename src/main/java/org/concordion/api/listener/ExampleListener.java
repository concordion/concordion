package org.concordion.api.listener;

import java.util.EventListener;

import org.concordion.api.ResultRecorder;

public interface ExampleListener extends EventListener {

    void beforeExample(ExampleEvent event);

    void afterExample(ExampleEvent event, ResultRecorder resultRecorder);
}
