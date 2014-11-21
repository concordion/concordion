package org.concordion.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

public class CachedRunResults {

    private enum RunStatus {
        NOT_STARTED,
        RUNNING,
        FINISHED
    }

    private class CachedRunSummary {
        private RunStatus status;
        private ResultSummary resultSummary;
        public CachedRunSummary(RunStatus runStatus, Class<?> fixtureClass) {
            status = runStatus;
            SingleResultSummary singleResultSummary = new SingleResultSummary(Result.IGNORED);
//            singleResultSummary.setSpecificationDescription("In progress result summary for " + fixtureClass.getName());
            this.resultSummary = singleResultSummary;
        }
    }

	Map<Class<?>, CachedRunSummary> map = new ConcurrentHashMap<Class<?>, CachedRunSummary>();

    public synchronized ResultSummary startRun(Class<? extends Object> fixtureClass) {


        CachedRunSummary runSummary = map.get(fixtureClass);
        if (runSummary != null) {
            return runSummary.resultSummary;
        }

        System.err.println("Starting fixture for class" + fixtureClass.getName());

        runSummary = new CachedRunSummary(RunStatus.RUNNING, fixtureClass);
        map.put(fixtureClass, runSummary);
        return null;
    }

    public synchronized void finishRun(Class<?> fixtureClass, ResultSummary resultSummary) {
        CachedRunSummary runSummary = map.get(fixtureClass);
        if (runSummary == null) {
            runSummary = new CachedRunSummary(RunStatus.FINISHED, fixtureClass);
        }
        runSummary.status = RunStatus.FINISHED;
        runSummary.resultSummary = resultSummary;

        map.put(fixtureClass, runSummary);
	}

}
