package org.concordion.api.listener;

/**
 * @since 2.0.2
 */
public interface OuterExampleListener {
    void beforeOuterExample(OuterExampleEvent event);
    void afterOuterExample(OuterExampleEvent event);
}
