package org.concordion.internal.command;

import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;
import org.concordion.api.Runner;
import org.concordion.internal.FailFastException;

public class SequentialRunStrategy implements RunStrategy {

    @Override
	public void call(final Runner runner, final Resource resource, final String href, final ResultAnnouncer announcer, final ResultRecorder resultRecorder) {
        try {
        	final ResultSummary result = runner.execute(resource, href);
            announcer.announce(result);
            resultRecorder.record(result);
        } catch (final FailFastException e) {
            throw e;
        } catch (final Throwable e) {
            announcer.announceException(e);
            resultRecorder.record(Result.EXCEPTION);
        }
    }
}
