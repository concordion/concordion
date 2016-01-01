package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;

public class ThrowableCaughtPublisher implements ThrowableCaughtListener {
    
	private List<ThrowableCaughtListener> listeners = new ArrayList<ThrowableCaughtListener>();
    
    public void addThrowableListener(ThrowableCaughtListener listener) {
        listeners.add(listener);
    }

    public void removeThrowableListener(ThrowableCaughtListener listener) {
        listeners.remove(listener);
    }

    public void throwableCaught(ThrowableCaughtEvent event) {
        for (ThrowableCaughtListener listener : listeners) {
			listener.throwableCaught(event);
		}
    }

}
