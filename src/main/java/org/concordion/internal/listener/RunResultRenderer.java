package org.concordion.internal.listener;

import org.concordion.api.Source;
import org.concordion.api.listener.*;

public class RunResultRenderer extends ThrowableRenderer implements RunListener {

    public RunResultRenderer(Source resourceSource) {
        super(resourceSource);
    }

    @Override
    public void runStarted(RunStartedEvent runStartedEvent) {
    }

    public void successReported(RunSuccessEvent event) {
        event.getElement().addStyleClass("success").appendNonBreakingSpaceIfBlank();
    }

    public void failureReported(RunFailureEvent event) {
        event.getElement().addStyleClass("failure").appendNonBreakingSpaceIfBlank();
    }

    public void ignoredReported(RunIgnoreEvent event) {
        event.getElement().addStyleClass("ignored").appendNonBreakingSpaceIfBlank();
    }

}
