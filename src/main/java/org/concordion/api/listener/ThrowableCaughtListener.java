package org.concordion.api.listener;

import java.util.EventListener;

public interface ThrowableCaughtListener extends EventListener {

    void throwableCaught(ThrowableCaughtEvent event);
}
