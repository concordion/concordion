package org.concordion.internal.command;

import org.concordion.api.Result;


public interface ResultAnnouncer {
    void announce(Result result);
    void announceException(Throwable e);
}