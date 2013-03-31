package org.concordion.internal.command;

import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.internal.util.Announcer;

public class ThrowableCaughtPublisher implements ThrowableCaughtListener {
    private Announcer<ThrowableCaughtListener> listeners = Announcer.to(ThrowableCaughtListener.class);
    
    public void addThrowableListener(ThrowableCaughtListener listener) {
        listeners.addListener(listener);
    }

    public void removeThrowableListener(ThrowableCaughtListener listener) {
        listeners.removeListener(listener);
    }

    public void throwableCaught(ThrowableCaughtEvent event) {
        listeners.announce().throwableCaught(event);
    }

}
