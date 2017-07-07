package org.concordion.api.listener;

import java.util.EventListener;

/**
 * @since 2.0.0
 */
public interface ExampleListener extends EventListener {

    void beforeExample(ExampleEvent event);
    void afterExample(ExampleEvent event);
}
