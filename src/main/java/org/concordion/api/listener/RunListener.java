package org.concordion.api.listener;


public interface RunListener extends ThrowableCaughtListener{

    void runStarted(RunStartedEvent runStartedEvent);

    void successReported(RunSuccessEvent runSuccessEvent);

    void failureReported(RunFailureEvent runFailureEvent);

    void ignoredReported(RunIgnoreEvent runIgnoreEvent);

}
