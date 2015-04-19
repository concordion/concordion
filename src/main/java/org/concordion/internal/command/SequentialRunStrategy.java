package org.concordion.internal.command;

import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.RunStrategy;
import org.concordion.api.Runner;
import org.concordion.internal.FailFastException;

public class SequentialRunStrategy implements RunStrategy {

    public void call(final Runner runner, final Resource resource, final String href, ResultAnnouncer announcer, ResultRecorder resultRecorder) {
        try {    
            Result result = runner.execute(resource, href).getResult();
            announcer.announce(result);
            resultRecorder.record(result);
        } catch (FailFastException e) { 
            throw e;
        } catch (Throwable e) {
            announcer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }
    }
}
