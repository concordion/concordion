package org.concordion.internal.listener;

import org.concordion.api.listener.RunFailureEvent;
import org.concordion.api.listener.RunIgnoreEvent;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.RunSuccessEvent;
import org.concordion.internal.util.IOUtil;

public class RunResultRenderer extends ThrowableRenderer implements RunListener {

    public RunResultRenderer(IOUtil ioUtil) {
		super(ioUtil);
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
