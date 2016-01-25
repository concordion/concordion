package org.concordion.internal.command;

import org.concordion.api.*;

public class SequentialRunStrategy implements RunStrategy {

	public void call(Runner runner, Resource resource, String href, ResultAnnouncer announcer, ResultRecorder resultRecorder) {
        try {
        	ResultSummary result = runner.execute(resource, href);
            announcer.announce(result);
            resultRecorder.record(result);
        } catch (FailFastException e) {
            announcer.announceException(e);
            resultRecorder.record(Result.EXCEPTION);
            // we let fail fast exceptions percolate up through the system
            throw e;
        } catch (Throwable e) {
            announcer.announceException(e);
            resultRecorder.record(Result.EXCEPTION);
        }
    }
}
