package org.concordion.api.listener;

import java.util.EventListener;

public interface ConcordionBuildListener extends EventListener {
    public void concordionBuilt(ConcordionBuildEvent event);
}
