package org.concordion.api.listener;

import java.util.EventListener;

public interface ExampleListener extends EventListener {

    void beforeExample(ExampleEvent event);

    void afterExample(ExampleEvent event);
}
