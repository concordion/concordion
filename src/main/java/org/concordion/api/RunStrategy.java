package org.concordion.api;


public interface RunStrategy {

    void call(Runner runner, Resource resource, String href, ResultAnnouncer announcer, ResultRecorder resultRecorder);
}