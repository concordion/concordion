package org.concordion.api.listener;

import java.util.EventListener;


public interface AssertListener extends EventListener {

    void successReported(AssertSuccessEvent event);
    
    void failureReported(AssertFailureEvent event);

}