package org.concordion.internal.command;

import org.concordion.api.Resource;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Runner;

public interface RunStrategy {

    void call(Runner runner, Resource resource, String href, ResultAnnouncer announcer, ResultRecorder resultRecorder);
}