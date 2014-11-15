package org.concordion.api.listener;

import java.util.EventListener;

public interface SetListener extends EventListener {

    void setCompleted(SetEvent event);

}
