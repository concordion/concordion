package org.concordion.api;

public interface ResultAnnouncer {
    void announceException(Throwable e);
	void announce(ResultSummary result);
}