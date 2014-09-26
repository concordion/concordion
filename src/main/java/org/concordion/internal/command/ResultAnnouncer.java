package org.concordion.internal.command;

import org.concordion.api.ResultSummary;


public interface ResultAnnouncer {
    void announceException(Throwable e);
	void announce(ResultSummary result);
}