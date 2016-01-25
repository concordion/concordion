package org.concordion.api;

import org.concordion.internal.command.ResultAnnouncer;


public interface RunStrategy {

    void call(Runner runner, Resource resource, String href, ResultAnnouncer announcer, ResultRecorder resultRecorder);
}